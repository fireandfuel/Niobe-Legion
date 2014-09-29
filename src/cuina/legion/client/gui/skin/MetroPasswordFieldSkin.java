package cuina.legion.client.gui.skin;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.sun.javafx.scene.control.skin.TextFieldSkin;

/**
 * Created by pedro_000 on 12/15/13.
 */
public class MetroPasswordFieldSkin extends TextFieldWithButtonSkin
{
	private boolean shouldMaskText = true;

	public MetroPasswordFieldSkin(TextField textField)
	{
		super(textField);
	}

	@Override
	protected void rightButtonPressed()
	{
		TextField textField = this.getSkinnable();
		this.shouldMaskText = false;
		textField.setText(textField.getText());
		this.shouldMaskText = true;
	}

	@Override
	protected void rightButtonReleased()
	{
		TextField textField = this.getSkinnable();
		textField.setText(textField.getText());
		textField.end();
	}

	@Override
	protected String maskText(String txt)
	{
		if(this.getSkinnable() instanceof PasswordField && this.shouldMaskText)
		{
			int n = txt.length();
			StringBuilder passwordBuilder = new StringBuilder(n);
			for(int i = 0; i < n; i++)
			{
				passwordBuilder.append(TextFieldSkin.BULLET);
			}

			return passwordBuilder.toString();
		} else
		{
			return txt;
		}
	}
}
