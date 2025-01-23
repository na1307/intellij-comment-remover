using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Psi.Tree;

namespace CommentRemover;

[Action(typeof(Properties.Resources), nameof(Properties.Resources.PlaceholderText),
    DescriptionResourceName = nameof(Properties.Resources.PlaceholderText))]
public sealed class RemoveSelectedDocCommentAction : RemoveSelectedCommentActionBase<IDocCommentBlock> {
    protected override IEnumerable<ITreeNode> AdditionalNodes { get; } = [];
}
