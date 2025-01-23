using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Psi.Tree;

namespace CommentRemover;

[Action(typeof(Properties.Resources), nameof(Properties.Resources.PlaceholderText),
    DescriptionResourceName = nameof(Properties.Resources.PlaceholderText))]
public sealed class RemoveSelectedCommentAction : RemoveSelectedCommentActionBase<ICommentNode> {
    protected override IEnumerable<ITreeNode> AdditionalNodes => Childrens!.OfType<IDocCommentBlock>();
}
