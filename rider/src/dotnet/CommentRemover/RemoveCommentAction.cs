using JetBrains.Annotations;
using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.VB;

namespace CommentRemover;

public abstract class RemoveCommentAction : IExecutableAction {
    public bool Update(IDataContext context, ActionPresentation presentation, [InstantHandle] DelegateUpdate nextUpdate) {
        var file = context.GetData(PsiDataConstants.SOURCE_FILE);
        var language = file?.LanguageType;

        return language?.Name is CSharpLanguage.Name or VBLanguage.Name or "CPP";
    }

    public abstract void Execute(IDataContext context, DelegateExecute nextExecute);

    protected static IEnumerable<ITreeNode> GetAllChildrens(ITreeNode node) {
        foreach (var child in node.Children()) {
            yield return child;

            foreach (var childnode in GetAllChildrens(child)) {
                yield return childnode;
            }
        }
    }
}
