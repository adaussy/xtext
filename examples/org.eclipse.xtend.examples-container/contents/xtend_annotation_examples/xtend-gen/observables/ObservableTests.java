package observables;

import observables.Observable;
import org.eclipse.xtend.core.compiler.batch.XtendCompilerTester;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.junit.Test;

@SuppressWarnings("all")
public class ObservableTests {
  @Extension
  private XtendCompilerTester compilerTester = new Function0<XtendCompilerTester>() {
    public XtendCompilerTester apply() {
      XtendCompilerTester _newXtendCompilerTester = XtendCompilerTester.newXtendCompilerTester(Observable.class);
      return _newXtendCompilerTester;
    }
  }.apply();
  
  @Test
  public void testObservable() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("import observables.Observable");
    _builder.newLine();
    _builder.newLine();
    _builder.append("@Observable ");
    _builder.newLine();
    _builder.append("class Person {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("String name");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.beans.PropertyChangeListener;");
    _builder_1.newLine();
    _builder_1.append("import java.beans.PropertyChangeSupport;");
    _builder_1.newLine();
    _builder_1.append("import observables.Observable;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("@Observable");
    _builder_1.newLine();
    _builder_1.append("@SuppressWarnings(\"all\")");
    _builder_1.newLine();
    _builder_1.append("public class Person {");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("private String name;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public String getName() {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return this.name;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public void setName(final String name) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("String _oldValue = this.name;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("this.name = name;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("_propertyChangeSupport.firePropertyChange(\"name\", _oldValue, name);");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("private PropertyChangeSupport _propertyChangeSupport = new PropertyChangeSupport(this);");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public void addPropertyChangeListener(final PropertyChangeListener listener) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("this._propertyChangeSupport.addPropertyChangeListener(listener);");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public void removePropertyChangeListener(final PropertyChangeListener listener) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("this._propertyChangeSupport.removePropertyChangeListener(listener);");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.compilerTester.assertCompilesTo(_builder, _builder_1);
  }
}