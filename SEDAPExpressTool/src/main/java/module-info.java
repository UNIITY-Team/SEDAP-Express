module secmockup {

    exports de.bundeswehr.sedap.express.tool;
    exports de.bundeswehr.sedap.express.tool.simulators.contact;
    exports de.bundeswehr.sedap.express.tool.simulators.ownunit;

    opens de.bundeswehr.sedap.express.tool;
    opens de.bundeswehr.sedap.express.tool.simulators.contact;
    opens de.bundeswehr.sedap.express.tool.simulators.ownunit;

    requires java.desktop;
    requires javafx.swing;
    requires transitive jdk.httpserver;
    requires transitive javafx.graphics;
    requires transitive javafx.fxml;
    requires transitive javafx.controls;
    requires transitive jogamp.fat;
    requires transitive WorldWindJava;
    requires transitive sedapexpress;

}