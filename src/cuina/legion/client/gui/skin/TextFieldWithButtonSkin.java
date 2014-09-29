package cuina.legion.client.gui.skin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import com.sun.javafx.scene.control.skin.TextFieldSkin;

/**
 * Created by pedro_000 on 12/15/13.
 */
public class TextFieldWithButtonSkin extends TextFieldSkin
{
	private StackPane rightButton;
	private Region rightButtonGraphic;

	protected TextField textField;

	public TextFieldWithButtonSkin(TextField textField)
	{
		super(textField);

		this.textField = textField;

		this.rightButton = new StackPane();
		this.rightButton.getStyleClass().setAll("right-button");
		this.rightButton.setFocusTraversable(false);

		this.rightButtonGraphic = new Region();
		this.rightButtonGraphic.getStyleClass().setAll("right-button-graphic");
		this.rightButtonGraphic.setFocusTraversable(false);

		this.rightButtonGraphic.setMaxWidth(Region.USE_PREF_SIZE);
		this.rightButtonGraphic.setMaxHeight(Region.USE_PREF_SIZE);

		this.rightButton.setVisible(false);
		this.rightButtonGraphic.setVisible(false);

		this.rightButton.getChildren().add(this.rightButtonGraphic);
		this.getChildren().add(this.rightButton);

		this.setupListeners();
	}

	private void setupListeners()
	{

		final TextField textField = this.getSkinnable();
		this.rightButton.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(TextFieldWithButtonSkin.this.textField.isEditable()
						&& !TextFieldWithButtonSkin.this.textField.isDisabled())
				{
					TextFieldWithButtonSkin.this.rightButtonPressed();
				}
			}
		});
		this.rightButton.setOnMouseReleased(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(TextFieldWithButtonSkin.this.textField.isEditable()
						&& !TextFieldWithButtonSkin.this.textField.isDisabled())
				{
					TextFieldWithButtonSkin.this.rightButtonReleased();
				}
			}
		});

		textField.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue)
			{
				TextFieldWithButtonSkin.this.textChanged();
			}
		});
		textField.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue)
			{
				TextFieldWithButtonSkin.this.focusChanged();
			}
		});
	}

	protected void textChanged()
	{
		if(this.textField.getText() == null)
		{
			return;
		}

		this.rightButton.setVisible(!this.textField.getText().isEmpty());
		this.rightButtonGraphic.setVisible(!this.textField.getText().isEmpty());
	}

	protected void focusChanged()
	{
		if(this.textField.getText() == null || !TextFieldWithButtonSkin.this.textField.isEditable()
				|| TextFieldWithButtonSkin.this.textField.isDisabled())
		{
			return;
		}

		this.rightButton.setVisible(this.textField.isFocused()
				&& !this.textField.getText().isEmpty());
		this.rightButtonGraphic.setVisible(this.textField.isFocused()
				&& !this.textField.getText().isEmpty());
	}

	@Override
	protected void layoutInArea(Node child, double areaX, double areaY, double areaWidth,
			double areaHeight, double areaBaselineOffset, Insets margin, boolean fillWidth,
			boolean fillHeight, HPos halignment, VPos valignment)
	{
		if(child == this.rightButton)
		{
			final double clearGraphicWidth = this.snapSize(this.rightButtonGraphic.prefWidth(-1));

			this.rightButton.resize(clearGraphicWidth, areaHeight);
			this.positionInArea(this.rightButton, (areaX + areaWidth) - clearGraphicWidth, areaY,
					clearGraphicWidth, areaHeight, 0, HPos.CENTER, VPos.CENTER);
		} else
		{
			super.layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, margin,
					fillWidth, fillHeight, halignment, valignment);
		}
	}

	protected void rightButtonPressed()
	{
	}

	protected void rightButtonReleased()
	{

	}

}