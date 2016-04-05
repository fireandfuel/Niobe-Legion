/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MainController.java) is part of Niobe Legion (module niobe-legion-client).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.xml.stream.XMLStreamException;
import niobe.legion.client.Client;
import niobe.legion.client.gui.about.AboutController;
import niobe.legion.client.gui.platform.osx.MenuToolkit;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

public class MainController
{
    private Stage stage;
    private ResizeDragListener resizeDragListener;

    @FXML
    private Label topLabel;
    @FXML
    private ImageView icon;
    @FXML
    private Button userButton;
    @FXML
    private AnchorPane mainPane;

    private Object currentController;

    @FXML
    private void initialize() throws IOException
    {
        setupMenuBar();

        this.userButton.setPadding(new Insets(0));
        this.userButton.setOnAction(event -> {
            Popup popup = new Popup();
            Button logout = new Button(Client.getLocalisation("logout"));
            logout.setOnAction(buttonEvent -> this.logout());
            logout.setFocusTraversable(false);

            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.getContent().addAll(logout);

            Bounds bounds = this.userButton.localToScreen(this.userButton.getBoundsInLocal());
            popup.show(this.userButton, bounds.getMinX(), bounds.getMinY() + this.userButton.getHeight());
        });
        this.loadMask("/niobe/legion/client/fxml/connect/Connect.fxml");
    }

    public Object loadMask(final String maskURI) throws IOException
    {
        if(Platform.isFxApplicationThread())
        {
            URL location = MainController.class.getResource(maskURI);

            FXMLLoader loader = new FXMLLoader(location, Client.getLocalBundle());
            Node node = loader.load();
            Object controller = loader.getController();
            this.setCurrentContent(node, controller);

            return controller;
        } else
        {
            Object[] wrapper = new Object[1];

            Platform.runLater(() -> {
                URL location = MainController.class.getResource(maskURI);

                if(location != null)
                {
                    FXMLLoader loader = new FXMLLoader(location, Client.getLocalBundle());
                    try
                    {
                        Node node = loader.load();
                        Object controller = loader.getController();
                        this.setCurrentContent(node, controller);

                        wrapper[0] = controller;
                        synchronized(MainController.this)
                        {
                            MainController.this.notify();
                        }
                    } catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                } else
                {
                    Logger.error(LegionLogger.STDERR, "FXML mask " + maskURI + " not found.");
                }
            });

            synchronized(MainController.this)
            {
                while(wrapper[0] == null)
                {
                    try
                    {
                        MainController.this.wait();
                    } catch(InterruptedException e)
                    {
                    }
                }
            }

            return wrapper[0];
        }
    }

    public void setCurrentContent(final Node node, final Object controller)
    {
        if(Platform.isFxApplicationThread())
        {
            this.mainPane.getChildren().clear();
            this.mainPane.getChildren().add(node);
            this.currentController = controller;
            AnchorPane.setBottomAnchor(node, 7.0d);
            AnchorPane.setLeftAnchor(node, 7.0d);
            AnchorPane.setRightAnchor(node, 7.0d);
            AnchorPane.setTopAnchor(node, 7.0d);
        } else
        {
            Platform.runLater(() -> MainController.this.setCurrentContent(node, controller));
        }
    }

    public Object getCurrentController()
    {
        return this.currentController;
    }

    public Stage getStage()
    {
        return this.stage;
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
        this.resizeDragListener = ResizeDragListener.addResizeListener(stage);
        this.topLabel.setText(stage.getTitle());

        if(this.icon != null && this.stage != null && !this.stage.getIcons().isEmpty())
        {
            this.icon.setImage(this.stage.getIcons().get(0));
        }
    }

    public ICloseableDialogController showHeavyWeightDialog(final String maskURI, final String title, Modality modality,
                                                            boolean closeable) throws IOException
    {
        return this.showHeavyWeightDialog(maskURI, title, modality, closeable, -1, -1);
    }

    public ICloseableDialogController showHeavyWeightDialog(final String maskURI, final String title, Modality modality,
                                                            boolean closeable,
                                                            int width,
                                                            int height) throws IOException
    {
        if(Platform.isFxApplicationThread())
        {
            final Stage dialog = new Stage();
            if(modality != Modality.NONE)
            {
                dialog.initOwner(this.stage);
                dialog.initModality(modality);
            }
            dialog.setTitle(title);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.setResizable(true);

            URL location;
            if(System.getProperty("os.name").toLowerCase().contains("mac"))
            {
                location = MainController.class.getResource("/niobe/legion/client/fxml/DialogSceneOSX.fxml");
            } else
            {
                location = MainController.class.getResource("/niobe/legion/client/fxml/DialogScene.fxml");
            }

            FXMLLoader loader = new FXMLLoader(location, Client.getLocalBundle());
            Scene scene = loader.load();
            if(width > -1)
            {
                dialog.setWidth(width);
            }
            if(height > -1)
            {
                dialog.setHeight(height);
            }
            DialogController controller = loader.getController();
            controller.setTitle(title);
            ICloseableDialogController childrenController = controller.loadMask(maskURI, closeable);

            childrenController.setStage(dialog);

            scene.getStylesheets()
                    .add(MainController.class.getResource("/niobe/legion/client/css/theme.css").toExternalForm());
            scene.setFill(null);

            dialog.setScene(scene);
            ResizeDragListener.addResizeListener(dialog);
            dialog.show();

            dialog.setX(this.stage.getX() + this.stage.getWidth() / 2 - dialog.getWidth() / 2);
            dialog.setY(this.stage.getY() + this.stage.getHeight() / 2 - dialog.getHeight() / 2);

            return childrenController;
        } else
        {
            // array to wrap the result
            final ICloseableDialogController[] wrapper = new ICloseableDialogController[1];

            Platform.runLater(() -> {
                try
                {
                    wrapper[0] = MainController.this.showHeavyWeightDialog(maskURI, title, modality, closeable);
                    synchronized(MainController.this)
                    {
                        MainController.this.notify();
                    }
                } catch(IOException e)
                {
                    Logger.exception(LegionLogger.STDERR, e);
                }
            });

            // wait for the dialog to load, we should be notified
            synchronized(MainController.this)
            {
                while(wrapper[0] == null)
                {
                    try
                    {
                        this.wait();
                    } catch(InterruptedException e)
                    {
                    }
                }
            }

            return wrapper[0];
        }
    }

    public ObservableValue<ButtonType> showLightweightDialog(final String message, ButtonType... buttons)
    {
        final SimpleObjectProperty<ButtonType> observableButton = new SimpleObjectProperty<ButtonType>();

        if(Platform.isFxApplicationThread())
        {
            final Stage dialog = new Stage();
            dialog.initOwner(this.stage);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.setResizable(false);

            HBox buttonBox = new HBox(5);
            buttonBox.setAlignment(Pos.CENTER);

            if(buttons != null && buttons.length > 0)
            {
                Stream.of(buttons).forEach(type -> {
                    Button button = new Button(type.getText());
                    button.setOnAction(event -> {
                        dialog.close();
                        observableButton.set(type);
                    });
                    buttonBox.getChildren().add(button);
                });
            } else
            {
                Button okButton = new Button("OK");
                okButton.setOnAction(event -> {
                    dialog.close();
                    observableButton.set(ButtonType.OK);
                });
                okButton.requestFocus();
                buttonBox.getChildren().add(okButton);
            }

            Label messageLabel = new Label(message);
            messageLabel.setTextAlignment(TextAlignment.CENTER);

            VBox content = new VBox(35, messageLabel, buttonBox);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(10));
            content.setId("mainPane");

            BorderPane borderPane = new BorderPane();

            AnchorPane draggableTopPane = new AnchorPane();
            draggableTopPane.setId("draggableTopPane");

            HBox hBox = new HBox(2.0d);
            hBox.setAlignment(Pos.CENTER);
            AnchorPane.setBottomAnchor(hBox, 0d);
            AnchorPane.setTopAnchor(hBox, 0d);
            AnchorPane.setLeftAnchor(hBox, 8d);
            AnchorPane.setRightAnchor(hBox, 8d);

            Label titleLabel = new Label(this.stage.getTitle());
            titleLabel.setId("topLabel");
            titleLabel.setAlignment(Pos.CENTER);
            titleLabel.setTextAlignment(TextAlignment.CENTER);
            hBox.getChildren().add(titleLabel);

            draggableTopPane.getChildren().add(hBox);

            borderPane.setTop(draggableTopPane);
            borderPane.setCenter(content);

            AnchorPane rootPane = new AnchorPane();
            rootPane.setId("rootPane");
            rootPane.getChildren().add(borderPane);

            Scene scene = new Scene(rootPane);

            scene.setFill(null);
            scene.getStylesheets()
                    .add(MainController.class.getResource("/niobe/legion/client/css/theme.css").toExternalForm());

            dialog.setScene(scene);
            ResizeDragListener.addResizeListener(dialog);

            dialog.show();

            dialog.setX(this.stage.getX() + this.stage.getWidth() / 2 - dialog.getWidth() / 2);
            dialog.setY(this.stage.getY() + this.stage.getHeight() / 2 - dialog.getHeight() / 2);
        } else
        {
            Platform.runLater(() -> {
                ObservableValue<ButtonType> innerObservableValue = MainController.this.showLightweightDialog(message);
                innerObservableValue.addListener((observable, oldValue, newValue) -> observableButton.set(newValue));
            });

        }
        return observableButton;
    }

    public ObservableValue<String> showLightweightTextInputDialog(final String message, boolean singleLine)
    {
        SimpleStringProperty stringProperty = new SimpleStringProperty();

        if(Platform.isFxApplicationThread())
        {
            final Stage dialog = new Stage();
            dialog.initOwner(this.stage);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.setResizable(false);

            HBox buttonBox = new HBox(5);

            TextInputControl textField = singleLine ? new TextField() : new TextArea();

            Button okButton = new Button("OK");
            okButton.setOnAction(event -> {
                dialog.close();
                stringProperty.set(textField.getText());
            });
            okButton.requestFocus();
            buttonBox.getChildren().add(okButton);

            Label messageLabel = new Label(message);
            messageLabel.setTextAlignment(TextAlignment.CENTER);

            VBox content = new VBox(35, messageLabel, textField, buttonBox);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(10));
            content.setId("mainPane");

            BorderPane borderPane = new BorderPane();

            AnchorPane draggableTopPane = new AnchorPane();
            draggableTopPane.setId("draggableTopPane");

            HBox hBox = new HBox(2.0d);
            hBox.setAlignment(Pos.CENTER);
            AnchorPane.setBottomAnchor(hBox, 0d);
            AnchorPane.setTopAnchor(hBox, 0d);
            AnchorPane.setLeftAnchor(hBox, 8d);
            AnchorPane.setRightAnchor(hBox, 8d);

            Label titleLabel = new Label(this.stage.getTitle());
            titleLabel.setId("topLabel");
            titleLabel.setAlignment(Pos.CENTER);
            titleLabel.setTextAlignment(TextAlignment.CENTER);
            hBox.getChildren().add(titleLabel);

            draggableTopPane.getChildren().add(hBox);

            borderPane.setTop(draggableTopPane);
            borderPane.setCenter(content);

            AnchorPane rootPane = new AnchorPane();
            rootPane.setId("rootPane");
            rootPane.getChildren().add(borderPane);

            Scene scene = new Scene(rootPane);

            scene.setFill(null);
            scene.getStylesheets()
                    .add(MainController.class.getResource("/niobe/legion/client/css/theme.css").toExternalForm());

            dialog.setScene(scene);
            ResizeDragListener.addResizeListener(dialog);

            dialog.show();

            dialog.setX(this.stage.getX() + this.stage.getWidth() / 2 - dialog.getWidth() / 2);
            dialog.setY(this.stage.getY() + this.stage.getHeight() / 2 - dialog.getHeight() / 2);
        } else
        {
            Platform.runLater(() -> {
                ObservableValue<String> innerObservableValue = MainController.this
                        .showLightweightTextInputDialog(message, singleLine);
                innerObservableValue.addListener((observable, oldValue, newValue) -> stringProperty.set(newValue));
            });
        }

        return stringProperty;
    }

    public void setUserButton(String userName)
    {
        if(Platform.isFxApplicationThread())
        {
            if(userName != null && !userName.isEmpty())
            {
                this.userButton.setText(userName);
                this.userButton.setVisible(true);
            } else
            {
                this.userButton.setText("");
                this.userButton.setVisible(false);
            }
        } else
        {
            Platform.runLater(() -> this.setUserButton(userName));
        }
    }

    private void setupMenuBar()
    {
        MenuToolkit toolkit = null;

        if(System.getProperty("os.name").toLowerCase().contains("mac"))
        {
            toolkit = MenuToolkit.toolkit();
        }

        MenuBar bar = new MenuBar();

        // Application Menu
        // TBD: services menu
        Menu appMenu = new Menu(Client.APP_NAME); // Name for appMenu can't be set at
        // Runtime
        MenuItem aboutItem = new MenuItem(String.format(Client.getLocalisation("about"), Client.APP_NAME));
        aboutItem.setOnAction(event -> this.showAbout());
        MenuItem prefsItem = new MenuItem(Client.getLocalisation("preferencesMenuItem"));
        if(toolkit != null)
        {
            appMenu.getItems().addAll(aboutItem,
                                      new SeparatorMenuItem(),
                                      prefsItem,
                                      new SeparatorMenuItem(),
                                      toolkit.createHideMenuItem(Client.APP_NAME),
                                      toolkit.createHideOthersMenuItem(),
                                      toolkit.createUnhideAllMenuItem(),
                                      new SeparatorMenuItem(),
                                      toolkit.createQuitMenuItem(Client.APP_NAME));
        }

        // User Menu
        Menu userMenu = new Menu(Client.getLocalisation("userMenu"));
        MenuItem changePasswordItem = new MenuItem(Client.getLocalisation("changePasswordMenuItem"));
        changePasswordItem.setDisable(true);
        MenuItem logoutItem = new MenuItem(Client.getLocalisation("logout"));
        logoutItem.setOnAction(event -> this.logout());
        logoutItem.setDisable(true);
        userMenu.getItems().addAll(changePasswordItem, new SeparatorMenuItem(), logoutItem);

        if(toolkit != null)
        {
            // Window Menu
            // TBD standard window menu items
            Menu windowMenu = new Menu(Client.getLocalisation("windowMenu"));
            windowMenu.getItems().addAll(toolkit.createMinimizeMenuItem(),
                                         toolkit.createZoomMenuItem(),
                                         toolkit.createCycleWindowsItem(),
                                         new SeparatorMenuItem(),
                                         toolkit.createBringAllToFrontItem());

            // Help Menu
            Menu helpMenu = new Menu(Client.getLocalisation("helpMenu"));

            bar.getMenus().addAll(appMenu, userMenu, windowMenu, helpMenu);

            toolkit.autoAddWindowMenuItems(windowMenu);
            toolkit.setGlobalMenuBar(bar);
        }
    }

    @FXML
    private void maximize()
    {
        if(this.resizeDragListener != null)
        {
            this.resizeDragListener.toggleMaximized();
        }
    }

    @FXML
    private void iconfy()
    {
        this.stage.setIconified(true);
    }

    @FXML
    private void close() throws IOException, XMLStreamException
    {
        Client.close();
    }

    private void logout()
    {
        this.showLightweightDialog(Client.getLocalisation("logoutQuestion"), ButtonType.YES, ButtonType.NO)
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue == ButtonType.YES)
                    {
                        try
                        {
                            Client.getCommunicator().logout();
                        } catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @FXML
    private void showAbout()
    {
        Stage stage = new Stage(StageStyle.TRANSPARENT);

        FXMLLoader loader = new FXMLLoader(this.getClass()
                                                   .getResource("/niobe/legion/client/fxml/about/AboutOSX.fxml"),
                                           Client.getLocalBundle());

        try
        {
            Scene scene = loader.load();
            AboutController controller = loader.getController();

            scene.getStylesheets()
                    .add(this.getClass().getResource("/niobe/legion/client/css/theme.css").toExternalForm());
            scene.setFill(null);
            stage.setTitle(String.format(Client.getLocalisation("about"), Client.APP_NAME));
            controller.setTitle(stage.getTitle());
            stage.setScene(scene);
            controller.setStage(stage);
            stage.setWidth(600);
            stage.setHeight(400);
            ResizeDragListener.addResizeListener(stage);
            stage.show();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
