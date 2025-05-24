using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl;
using JetBrains.Util;

namespace CommentRemover;

internal static class Utils {
    public static IEnumerable<ITreeNode> GetAllChildrens(ITreeNode node) {
        foreach (var child in node.Children()) {
            yield return child;

            foreach (var childnode in GetAllChildrens(child)) {
                yield return childnode;
            }
        }
    }

    public static IEnumerable<TextRange>? TextControlToSelectionRange(ITextControl textControl) {
        var selection = textControl.Selection;

        return selection.HasSelection() ? selection.Ranges.Value.Select(r => r.ToDocRangeNormalized()) : null;
    }

    public static bool IsNodeInRanges(ITreeNode node, IEnumerable<TextRange> ranges) {
        var startOffset = node.GetTreeStartOffset().Offset;
        var endOffset = node.GetTreeEndOffset().Offset;

        return ranges.Any(r => startOffset >= r.StartOffset && endOffset <= r.EndOffset);
    }
}
