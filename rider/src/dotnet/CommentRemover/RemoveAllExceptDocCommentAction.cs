using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Transactions;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Util;

namespace CommentRemover;

[Action(typeof(Properties.Resources), nameof(Properties.Resources.PlaceholderText),
    DescriptionResourceName = nameof(Properties.Resources.PlaceholderText))]
public sealed class RemoveAllExceptDocCommentAction : RemoveCommentActionBase {
    public override void Execute(IDataContext context, DelegateExecute nextExecute) {
        var sourceFile = context.GetData(PsiDataConstants.SOURCE_FILE);
        var file = sourceFile?.GetPrimaryPsiFile();

        if (file is null) {
            return;
        }

        var childrens = Utils.GetAllChildrens(file).ToArray();

        var comments = childrens.OfType<ICommentNode>()
            .Where(n => n is not JetBrains.ReSharper.Psi.CSharp.Tree.IDocCommentNode
                and not JetBrains.ReSharper.Psi.VB.Tree.IDocComment).ToArray();

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
