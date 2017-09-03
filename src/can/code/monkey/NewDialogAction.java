package can.code.monkey;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class NewDialogAction extends AnAction {

    private static final String KEY_NAME = "#_#_name_#_#";
    private static final String KEY_PACKAGE = "#_#_package_#_#";

    @Override
    public void actionPerformed(AnActionEvent event) {

        Project project = event.getData(PlatformDataKeys.PROJECT);
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        if (null == file) {
            Messages.showMessageDialog(project, "Wrong place for performing this action.", "Error", Messages.getErrorIcon());
            return;
        }

        String path = file.getPath();
        String name = Messages.showInputDialog(project, "Enter new dialog name (Fragment, Model endings are added automatically)." +  event.getData(PlatformDataKeys.VIRTUAL_FILE).getPath(), "Generate code for new dialog", Messages.getQuestionIcon());

        if (null != name) {
            String packageName = path.replaceAll(".*/src/","package ").replaceAll("/",".");
            String fragmentName = name + "Fragment";
            String fragmentPath = path + "/" + fragmentName + ".java";

            String modelName = name + "Model";
            String modelPath = path + "/" + modelName + ".java";


            String layoutName = String.join("_", fragmentName.split("(?<=.)(?=\\p{Lu})")).toLowerCase() + ".xml";
            String layoutPath = path.replaceAll("/src.*", "/res/layout/" + layoutName);

            try {

                String fragmentContent = Temlates.TEMPLATE_FRAGMENT.replaceAll(KEY_PACKAGE, packageName);
                fragmentContent = fragmentContent.replaceAll(KEY_NAME, name);
                String modelContent = Temlates.TEMPLATE_MODEL.replaceAll(KEY_PACKAGE, packageName);
                modelContent = modelContent.replaceAll(KEY_NAME, name);
                String layoutContent = Temlates.TEMPALTE_LAYOUT.replaceAll(KEY_NAME, name);

                FileUtils.fileWrite(fragmentPath, fragmentContent);
                FileUtils.fileWrite(modelPath, modelContent);
                FileUtils.fileWrite(layoutPath, layoutContent);

                Messages.showMessageDialog(project, "All required files where generated.", "Success", Messages.getInformationIcon());
            } catch (IOException ex) {
                Messages.showMessageDialog(project, "File can't be created. Nothing was created.", "Error", Messages.getErrorIcon());
                File fragment = new File(fragmentPath);
                File model = new File(modelPath);
                File layout = new File(layoutPath);
                if (fragment.exists()) {
                    fragment.deleteOnExit();
                }
                if (model.exists()) {
                    model.deleteOnExit();
                }
                if (layout.exists()) {
                    layout.deleteOnExit();
                }
            }
        }
    }
}
