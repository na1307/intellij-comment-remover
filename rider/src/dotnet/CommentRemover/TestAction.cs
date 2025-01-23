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

#pragma warning disable CS0618 // 형식 또는 멤버는 사용되지 않습니다.
[Action("PLACEHOLDER")]
#pragma warning restore CS0618 // 형식 또는 멤버는 사용되지 않습니다.
public sealed class TestAction : ExecutableAction {
    public override void Execute(IDataContext context, DelegateExecute nextExecute) {
        var sourceFile = context.GetData(PsiDataConstants.SOURCE_FILE);
        var file = sourceFile?.GetPrimaryPsiFile();

        if (file is null) {
            return;
        }

        var childrens = GetAllChildrens(file).ToArray();

        var comments = childrens.OfType<ICommentNode>().Cast<ITreeNode>()
            .Concat(childrens.OfType<IDocCommentBlock>()).ToArray();

        if (comments.Length == 0) {
            return;
        }

        using (WriteLockCookie.Create()) {
            using (new PsiTransactionCookie(file.GetPsiServices(), DefaultAction.Commit, "Delete all comments")) {
                foreach (var comment in comments) {
                    ModificationUtil.DeleteChild(comment);
                }
            }
        }
    }
}
