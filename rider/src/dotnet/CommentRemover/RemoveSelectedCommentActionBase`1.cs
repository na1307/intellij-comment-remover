using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Transactions;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.TextControl.DataContext;
using JetBrains.Util;

namespace CommentRemover;

public abstract class RemoveSelectedCommentActionBase<TNode> : RemoveCommentActionBase where TNode : ITreeNode {
    protected ITreeNode[]? Childrens { get; private set; }

    protected abstract IEnumerable<ITreeNode> AdditionalNodes { get; }

    public sealed override void Execute(IDataContext context, DelegateExecute nextExecute) {
        var sourceFile = context.GetData(PsiDataConstants.SOURCE_FILE);
        var file = sourceFile?.GetPrimaryPsiFile();

        if (file is null) {
            return;
        }

        var textControl = context.GetData(TextControlDataConstants.TEXT_CONTROL);

        if (textControl is null) {
            return;
        }

        var ranges = Utils.TextControlToSelectionRange(textControl);

        if (ranges is null) {
            return;
        }

        Childrens = Utils.GetAllChildrens(file).ToArray();

        var comments = Childrens.OfType<TNode>().Cast<ITreeNode>().Concat(AdditionalNodes)
            .Where(n => Utils.IsNodeinRanges(n, ranges)).ToArray();

        if (comments.Length == 0) {
            return;
        }

        using (WriteLockCookie.Create()) {
            using (new PsiTransactionCookie(file.GetPsiServices(), DefaultAction.Commit, "Remove comments")) {
                foreach (var comment in comments) {
                    ModificationUtil.DeleteChild(comment);
                }
            }
        }
    }
}
