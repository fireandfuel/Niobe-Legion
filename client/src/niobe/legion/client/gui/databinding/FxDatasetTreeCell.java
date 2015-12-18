/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (FxDatasetTreeCell.java) is part of Niobe Legion (module niobe-legion-client).
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;
import javafx.util.Callback;

public class FxDatasetTreeCell<S, T> extends TreeTableCell<S, T>
{
    private final CheckBox checkBox;

    private ObservableValue<Boolean> booleanProperty;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public FxDatasetTreeCell()
    {
        this(null);
    }

    public FxDatasetTreeCell(final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
    {
        this.getStyleClass().add("check-box-table-cell");

        this.checkBox = new CheckBox();

        // by default the graphic is null until the cell stops being empty
        this.setGraphic(null);

        this.setSelectedStateCallback(getSelectedProperty);
    }

    @Override
    protected void updateItem(T item, boolean empty)
    {
        super.updateItem(item, empty);

        if(item instanceof Boolean)
        {
            if(empty)
            {
                setText(null);
                setGraphic(null);
            } else
            {
                setGraphic(checkBox);

                if(booleanProperty instanceof BooleanProperty)
                {
                    checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
                }
                ObservableValue<?> obsValue = getSelectedProperty();
                if(obsValue instanceof BooleanProperty)
                {
                    booleanProperty = (ObservableValue<Boolean>) obsValue;
                    checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
                }

                checkBox.disableProperty().bind(Bindings.not(getTreeTableView().editableProperty()
                                                                     .and(getTableColumn().editableProperty())
                                                                     .and(editableProperty())));
            }
        } else if(item instanceof LocalDate)
        {
            if(empty)
            {
                setText(null);
                setGraphic(null);
            } else
            {
                LocalDate date = (LocalDate) item;
                setText(dateFormatter.format(date));
                setGraphic(null);
            }
        } else if(item instanceof LocalDateTime)
        {
            if(empty)
            {
                setText(null);
                setGraphic(null);
            } else
            {
                LocalDateTime date = (LocalDateTime) item;
                setText(dateTimeFormatter.format(date));
                setGraphic(null);
            }
        } else if(item instanceof LocalTime)
        {
            if(empty)
            {
                setText(null);
                setGraphic(null);
            } else
            {
                LocalTime date = (LocalTime) item;
                setText(timeFormatter.format(date));
                setGraphic(null);
            }
        } else
        {
            if(empty)
            {
                setText(null);
                setGraphic(null);
            } else
            {
                setText(item != null ? item.toString() : null);
                setGraphic(null);
            }
        }
    }

    // --- selected state callback property
    private ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallback = new SimpleObjectProperty<Callback<Integer, ObservableValue<Boolean>>>(
            this,
            "selectedStateCallback");

    /**
     * Property representing the {@link Callback} that is bound to by the
     * CheckBox shown on screen.
     */
    public final ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallbackProperty()
    {
        return selectedStateCallback;
    }

    /**
     * Sets the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final void setSelectedStateCallback(Callback<Integer, ObservableValue<Boolean>> value)
    {
        selectedStateCallbackProperty().set(value);
    }

    /**
     * Returns the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final Callback<Integer, ObservableValue<Boolean>> getSelectedStateCallback()
    {
        return selectedStateCallbackProperty().get();
    }

    private ObservableValue<?> getSelectedProperty()
    {
        return getSelectedStateCallback() != null ? getSelectedStateCallback().call(getIndex()) : getTableColumn()
                .getCellObservableValue(getIndex());
    }
}
