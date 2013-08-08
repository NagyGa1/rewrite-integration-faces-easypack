Java library that extends and simplifies the use of OCPsoft's Rewrite libraries when programming in JSF.
Key features are:
###Key features
1. Simplicity - you no longer need to annotate your fields and methods with multiple annotations: @Parameter or @RequestAction + @Deferred + @IgnorePostback, new annotations provide all the neccessary elements, use one annotation instead of two (minimum for JSF) or three.
2. More power - you can define url parameters and actions wherever you want in your ManagedBean's class hierarchy, which is not the case with core Rewrite libraries.
3. More reusability - you can now define as many URL joins for a single bean as you want thus reusing your bean's logic for multiple JSF views
