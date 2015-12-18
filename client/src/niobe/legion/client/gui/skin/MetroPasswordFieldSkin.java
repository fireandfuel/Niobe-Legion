/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MetroPasswordFieldSkin.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.skin;

import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
