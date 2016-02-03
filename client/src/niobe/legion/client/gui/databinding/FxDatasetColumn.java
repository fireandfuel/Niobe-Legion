/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (FxDatasetColumn.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.databinding;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FxDatasetColumn<C> extends TableColumn<FxDatasetWrapper, C>
{
    public FxDatasetColumn(final String key, final String name)
    {
        this(key, name, null, -1);
    }

    public FxDatasetColumn(final String key, final String name, int width)
    {
        this(key, name, null, width);
    }

    public FxDatasetColumn(final String key, final String name, boolean editable)
    {
        this(key, name, editable, null);
    }

    public FxDatasetColumn(final String key, final String name, boolean editable, int width)
    {
        this(key, name, editable, null, -1);
    }


    public FxDatasetColumn(final String key, final String name,
                           final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty, int width)
    {
        super(name);
        this.setCellValueFactory(column -> (ObservableValue<C>) column.getValue().getProperty(key));

        this.setCellFactory(param -> new FxDatasetCell<FxDatasetWrapper, C>(getSelectedProperty));

        if(width > -1)
        {
            this.setPrefWidth(width);
        }
    }

    public FxDatasetColumn(final String key, final String name, boolean editable,
                           final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
    {
        this(key, name, getSelectedProperty, -1);

        this.setEditable(true);
    }

    public FxDatasetColumn(final String key, final String name, boolean editable,
                           final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty, int width)
    {
        this(key, name, getSelectedProperty, width);

        this.setEditable(true);
    }
}
