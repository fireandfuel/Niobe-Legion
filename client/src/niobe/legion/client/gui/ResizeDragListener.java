package niobe.legion.client.gui;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ResizeDragListener implements EventHandler<MouseEvent>
{
	private static final int NONE  = 0x00;
	private static final int WEST  = 0x01;
	private static final int NORTH = 0x02;
	private static final int EAST  = 0x04;
	private static final int SOUTH = 0x08;

	private static final int BORDER = 8;

	private Stage stage;
	private int   state;
	private double startX      = 0d;
	private double startY      = 0d;
	private double dragX       = 0d;
	private double dragY       = 0d;
	private double startWidth  = 0d;
	private double startHeight = 0d;
	private boolean maximized;
	private boolean dragging;

	ResizeDragListener(Stage stage)
	{
		this.stage = stage;
		Scene scene = stage.getScene();

		this.startX = scene.getX();
		this.startY = scene.getY();
		this.startHeight = scene.getHeight();
		this.startWidth = scene.getWidth();
	}

	public static ResizeDragListener addResizeListener(Stage stage)
	{
		ResizeDragListener resizeListener = new ResizeDragListener(stage);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_RELEASED, resizeListener);
		ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
		for (Node child : children)
		{
			addListenerDeeply(child, resizeListener);
		}
		return resizeListener;
	}

	private static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener)
	{
		node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, listener);
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
		Scene scene = this.stage.getScene();

		double mouseEventX = mouseEvent.getSceneX();
		double mouseEventY = mouseEvent.getSceneY();
		double sceneWidth = scene.getWidth();
		double sceneHeight = scene.getHeight();

		if (MouseEvent.MOUSE_MOVED.equals(mouseEventType) && !maximized && !dragging)
		{
			this.state = NONE;

			if (mouseEventX < BORDER)
			{
				this.state += WEST;
			}
			if (mouseEventX > sceneWidth - BORDER)
			{
				this.state += EAST;
			}
			if (mouseEventY < BORDER)
			{
				this.state += NORTH;
			}
			if (mouseEventY > sceneHeight - BORDER)
			{
				this.state += SOUTH;
			}
		} else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType))
		{
			if (!this.maximized)
			{
				this.startX = this.stage.getWidth() - mouseEventX;
				this.startY = this.stage.getHeight() - mouseEventY;
				this.dragX = mouseEventX;
				this.dragY = mouseEventY;
			}

			if (mouseEvent.getClickCount() == 2)
			{
				toggleMaximized();
			}

		} else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType))
		{
			if (!this.dragging)
			{
				this.dragging = true;
			}
			if (this.state != NONE && !this.maximized)
			{
				if ((this.state & NORTH) == NORTH || (this.state & SOUTH) == SOUTH)
				{
					double minHeight =
							this.stage.getMinHeight() > (BORDER * 2) ? this.stage.getMinHeight() : (BORDER * 2);
					if ((state & NORTH) == NORTH)
					{
						if (stage.getHeight() > minHeight || mouseEventY < 0)
						{
							this.stage.setHeight(this.stage.getY() - mouseEvent.getScreenY() + this.stage.getHeight());
							this.stage.setY(mouseEvent.getScreenY());
						}
					} else
					{
						if (this.stage.getHeight() > minHeight ||
							mouseEventY + this.startY - this.stage.getHeight() > 0)
						{
							this.stage.setHeight(mouseEventY + this.startY);
						}
					}
				}

				if ((this.state & WEST) == WEST || (this.state & EAST) == EAST)
				{
					double minWidth = this.stage.getMinWidth() > (BORDER * 2) ? this.stage.getMinWidth() : (BORDER * 2);
					if ((this.state & WEST) == WEST)
					{
						if (stage.getWidth() > minWidth || mouseEventX < 0)
						{
							this.stage.setWidth(this.stage.getX() - mouseEvent.getScreenX() + this.stage.getWidth());
							this.stage.setX(mouseEvent.getScreenX());
						}
					} else
					{
						if (this.stage.getWidth() > minWidth || mouseEventX + this.startX - this.stage.getWidth() > 0)
						{
							this.stage.setWidth(mouseEventX + this.startX);
						}
					}
				}
			} else
			{
				if (!this.maximized && state == NONE)
				{
					this.stage.setX(mouseEvent.getScreenX() - dragX);
					this.stage.setY(mouseEvent.getScreenY() - dragY);
				}
			}
		} else if (MouseEvent.MOUSE_RELEASED.equals(mouseEventType) && this.dragging)
		{
			this.dragging = false;
		}
	}

	public void toggleMaximized()
	{
		this.maximized = !this.maximized;

		if (this.maximized)
		{
			ObservableList<Screen> screens = Screen.getScreensForRectangle(new Rectangle2D(this.stage.getX(),
																						   this.stage.getY(),
																						   this.stage.getWidth(),
																						   this.stage.getHeight()));
			Rectangle2D visualBounds = screens.get(0).getVisualBounds();

			this.startX = this.stage.getX();
			this.startY = this.stage.getY();
			this.startWidth = this.stage.getWidth();
			this.startHeight = this.stage.getHeight();

			this.stage.setX(visualBounds.getMinX());
			this.stage.setY(visualBounds.getMinY());
			this.stage.setWidth(visualBounds.getWidth());
			this.stage.setHeight(visualBounds.getHeight());
		} else
		{
			this.stage.setX(this.startX);
			this.stage.setY(this.startY);
			this.stage.setWidth(this.startWidth);
			this.stage.setHeight(this.startHeight);
		}
	}
}
