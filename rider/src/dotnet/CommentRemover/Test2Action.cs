using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Transactions;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.TextControl;
using JetBrains.TextControl.DataContext;
using JetBrains.Util;

namespace CommentRemover;

#pragma warning disable CS0618 // 형식 또는 멤버는 사용되지 않습니다.
[Action("PLACEHOLDER")]
#pragma warning restore CS0618 // 형식 또는 멤버는 사용되지 않습니다.
public sealed class Test2Action : ExecutableAction {
    public override void Execute(IDataContext context, DelegateExecute nextExecute) {
        var sourceFile = context.GetData(PsiDataConstants.SOURCE_FILE);
        var file = sourceFile?.GetPrimaryPsiFile();

        if (file is null) {
            return;
        }

        var textControl = context.GetData(TextControlDataConstants.TEXT_CONTROL);

        if (textControl is null) {
            return;
        }

        var ranges = TextControlToSelectionRange(textControl);

        if (ranges is null) {
            return;
        }

        var childrens = GetAllChildrens(file).ToArray();

        var comments = childrens.OfType<ICommentNode>().Cast<ITreeNode>()
            .Concat(childrens.OfType<IDocCommentBlock>()).Where(n => IsNodeinRanges(n, ranges)).ToArray();

        if (comments.Length == 0) {
            return;
        }

        using (WriteLockCookie.Create()) {
            using (new PsiTransactionCookie(file.GetPsiServices(), DefaultAction.Commit, "Delete selected comments")) {
                foreach (var comment in comments) {
                    ModificationUtil.DeleteChild(comment);
                }
            }
        }
    }

    private static IEnumerable<TextRange>? TextControlToSelectionRange(ITextControl textControl) {
        var selection = textControl.Selection;

        return selection.HasSelection() ? selection.Ranges.Value.Select(r => r.ToDocRangeNormalized()) : null;
    }

    private static bool IsNodeinRanges(ITreeNode node, IEnumerable<TextRange> ranges) {
        var startOffset = node.GetTreeStartOffset().Offset;
        var endOffset = node.GetTreeEndOffset().Offset;

        return ranges.Any(r => startOffset >= r.StartOffset && endOffset <= r.EndOffset);
    }
}
