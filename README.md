Java library that extends and simplifies the use of OCPsoft's [Rewrite](http://ocpsoft.org/rewrite/) libraries when programming in JSF.

###Key features
1. Simplicity - you no longer need to annotate your fields and methods with multiple annotations: `@Parameter` or `@RequestAction` + `@Deferred` + `@IgnorePostback`, new annotations provide all the neccessary elements, now you use only one annotation instead of two or three.
2. More power - you can define url parameters and actions wherever you want in your ManagedBean's class hierarchy, which is not the case with core Rewrite libraries.
3. More reusability - you can now define as many URL joins for a single bean as you want, thus reusing your bean's logic for multiple JSF pages.

###Installation
Place the distribution jar inside the **WEB-INF/lib** folder, along with **rewrite-servlet** and **rewrite-integration-faces** artifacts. That's all you'll need.

###How to use
Make following replacements:
`@Join` to `@URLJoin`, `@Parameter` to `@URLParameter`,`@RequestAction` to `@URLAction`.
And don't forget to remove obsoleted `@Deferred` and `@IgnorePostback` annotation, the library checks their presence and throws exceptions if found.
Here is a sample code:

        @javax.faces.bean.ManagedBean
        @javax.faces.bean.ViewScoped
        @URLJoins(joins = {
          @URLJoin(path = "/{lang}/{testVar}/", to = "/faces/page1.xhtml"),
          @URLJoin(path = "/{lang}/", to = "/faces/page2.xhtml")
        })
        public class TestBean extends LangSetter {
          @URLParameter(ingorePostback = true)
          private String testVar;
          // Getter and setter required
          @URLAction
          public void action1() {
          ...
          }
        }

        public class LangSetter {
          @URLParameter
          private String lang;
          // Getter and setter required
          @URLAction
          public void action2() {
          ...
          }
          @URLAction(views = {"/faces/page2.xhtml"}, after = Phase.RESTORE_VIEW)
          public void action3() {
          ...
          }
        }
