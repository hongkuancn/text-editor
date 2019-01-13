//TODO: bug in adding two sentinel
//TODO: word wrapping -> It is possible other lines has a alphabet start node
//TODO: cascade for wrapping
//TODO: removeNode in list
//TODO: delete char -> wrap
//TODO: scroll bar
//TODO: move up and down
//TODO: mouse click
//TODO: undo and redo
package editor;

import java.io.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Editor extends Application {
    private final Rectangle cursorBoundingBox;

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private static final int STARTING_FONT_SIZE = 20;
    private static final int MARGIN = 5;

    public static String inputFilename;
    public static String outputType;

    public int fontSize = STARTING_FONT_SIZE;
    public double fontHeight;
    public String fontName = "Verdana";
    public Text example;
    public double benchmarkHeight;
    private FastLinkedList textBuffer = new FastLinkedList();

    public Editor() {
        // example font for getHeight
        example = new Text(0,0,"a");
        example.setFont(Font.font(fontName, fontSize));
        example.setTextOrigin(VPos.TOP);
        benchmarkHeight = Math.round(example.getLayoutBounds().getHeight());
        cursorBoundingBox = new Rectangle(1, benchmarkHeight);
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {

        private Group root;
        /** An EventHandler to handle keys that get pressed. */
        KeyEventHandler(final Group root, int windowWidth, int windowHeight) {

            this.root = root;

            textBuffer.currentContent().setFont(Font.font(fontName, fontSize));
            textBuffer.currentContent().setX(MARGIN);
            textBuffer.currentContent().setY(0);
            root.getChildren().add(textBuffer.currentContent());
        }

        @Override
        public void handle(KeyEvent keyEvent) {
           if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {

                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {

                    textBuffer.addChar(characterTyped, fontName, fontSize);
                    moveCursor();
                    root.getChildren().add(textBuffer.currentContent());
                    keyEvent.consume();
                }

            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {

                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.UP) {
//                    fontSize += 5;
//                    displayText.setFont(Font.font(fontName, fontSize));
                } else if (code == KeyCode.DOWN) {
//                    fontSize = Math.max(0, fontSize - 5);
//                    displayText.setFont(Font.font(fontName, fontSize));
                } else if (code == KeyCode.LEFT) {
                    textBuffer.moveLeft();
                    moveCursor();
                } else if (code == KeyCode.RIGHT) {
                    textBuffer.moveRight();
                    moveCursor();
                } else if (code == KeyCode.BACK_SPACE) {
                    root.getChildren().remove(textBuffer.currentContent());
                    String s = textBuffer.deleteChar(benchmarkHeight);
                    moveCursor();
                    // When pressing RETURN, there are two characters \n\r.
                    if (s != null && s.charAt(0) == '\r') {
                        String r = textBuffer.deleteChar(benchmarkHeight);
                        moveCursor();
                    }

                } else if (code == KeyCode.ENTER) {
                    textBuffer.addChar(String.valueOf('\n'), fontName, fontSize);
                    root.getChildren().add(textBuffer.currentContent());
                    moveCursor();

//                    for (Object obj: textBuffer) {
//                        FastLinkedList.Node node = (FastLinkedList.Node) obj;
//                        System.out.println(node.getContent().getText());
//                        System.out.println(node.getContent().getY());
//                    }

                } else if (keyEvent.isShortcutDown()) {
                    if (keyEvent.getCode() == KeyCode.S) {

                        try {
                            FileWriter writer = new FileWriter(inputFilename);

                            for (Object obj: textBuffer) {
                                FastLinkedList.Node node = (FastLinkedList.Node) obj;
                                char charRead = node.getContent().getText().charAt(0);
                                writer.write(charRead);
                            }

                            System.out.println("Successfully saved file to " + inputFilename);
                            writer.close();

                        } catch (IOException ioException) {
                            System.out.println("Error when saving; exception was: " + ioException);
                        }
                    } else if (keyEvent.getCode() == KeyCode.P){
                        int xPos = (int) (textBuffer.currentContent().getX() + textBuffer.getCurrentNode().getWidth());
                        int yPos = (int) textBuffer.currentContent().getY();
                        System.out.println(xPos + ", " + yPos);
                    } else if (keyEvent.getCode() == KeyCode.MINUS) {
                        fontSize = Math.max(0, fontSize - 4);
                        resizeFont();
                    } else if (keyEvent.getCode() == KeyCode.PLUS || keyEvent.getCode() == KeyCode.EQUALS) {
                        fontSize += 4;
                        resizeFont();
                    }
                }
            }
        }

        private void resizeFont() {
            // this for loop should be a method in the FastLinkedList class
            for (Object obj: textBuffer) {
                FastLinkedList.Node node = (FastLinkedList.Node) obj;
                Text text = node.getContent();
                text.setFont(Font.font(fontName, fontSize));
                text.setX(node.getXwhenResizing());

                if (text.equals(textBuffer.currentContent())) {
                    // Adjust cursor
                    cursorBoundingBox.setX(node.getXwhenResizing() + node.getWidth());
                }

                // Is it safe this way? I want to calculate once.
                int i = 1;
                while (i > 0) {
                    fontHeight = node.getHeight();
                    i -= 1;
                }
            }

            cursorBoundingBox.setHeight(fontHeight);

        }

        private void moveCursor() {
            // Cursor is always at the right side of currentNode
            cursorBoundingBox.setX(textBuffer.currentContent().getX() + textBuffer.getCurrentNode().getWidth());
            cursorBoundingBox.setY(textBuffer.currentContent().getY());
        }

    }

    /** An EventHandler to handle changing the color of the rectangle. */
    public class cursorBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        cursorBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
            setPosition();
        }

        private void changeColor() {
            cursorBoundingBox.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        private void setPosition() {
            // starting cursor position
            cursorBoundingBox.setX(5);
            cursorBoundingBox.setY(0);
            textBuffer.startingPosition();
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    /** Makes the text bounding box change color periodically. */
    public void makeCursorColorChange() {
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        cursorBlinkEventHandler cursorChange = new cursorBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        // check the size of parameters.
        int sizeOfParameters = getParameters().getUnnamed().size();
        if (sizeOfParameters == 0) {
            System.out.println("An argument is required.");
            System.exit(1);
        } else if (sizeOfParameters == 1) {
            inputFilename = getParameters().getUnnamed().get(0);

            try {
                File inputFile = new File(inputFilename);
                if (!inputFile.exists()) {
                    System.out.println("Unable to open because file with name " + inputFilename
                            + " does not exist");
                    return;
                }
                FileReader reader = new FileReader(inputFile);
                BufferedReader bufferedReader = new BufferedReader(reader);

                int intRead = -1;
                while ((intRead = bufferedReader.read()) != -1) {
                    char charRead = (char) intRead;
                    //TODO: if char == \r continue
                    if (charRead == '\r') {
                        continue;
                    }
                    if (charRead == '\n') {
                        System.out.println(intRead);
                    }
                    textBuffer.addChar(String.valueOf(charRead), fontName, fontSize);
                    root.getChildren().add(textBuffer.currentContent());
                }

                System.out.println("Successfully opened file " + inputFilename);

                bufferedReader.close();
            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found! Exception was: " + fileNotFoundException);
            } catch (IOException ioException) {
                System.out.println("Error when opening; exception was: " + ioException);
            }
        } else if (sizeOfParameters == 2) {
            inputFilename = getParameters().getUnnamed().get(0);
            outputType = getParameters().getUnnamed().get(1);

            if (outputType == "debug") {
                try {
                    File inputFile = new File(inputFilename);
                    if (!inputFile.exists()) {
                        System.out.println("Unable to open because file with name " + inputFilename
                                + " does not exist");
                        return;
                    }
                    FileReader reader = new FileReader(inputFile);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    int intRead = -1;
                    while ((intRead = bufferedReader.read()) != -1) {
                        char charRead = (char) intRead;
                        textBuffer.addChar(String.valueOf(charRead), fontName, fontSize);
                        textBuffer.currentContent().setFont(Font.font(fontName, fontSize));
                        root.getChildren().add(textBuffer.currentContent());
                    }

                    System.out.println("Successfully opened file " + inputFilename);

                    bufferedReader.close();
                } catch (FileNotFoundException fileNotFoundException) {
                    System.out.println("File not found! Exception was: " + fileNotFoundException);
                } catch (IOException ioException) {
                    System.out.println("Error when opening; exception was: " + ioException);
                }
            } else {
                System.out.println("Invalid output type.");
            }
        } else if (sizeOfParameters > 2) {
            System.out.println("At most 2 arguments can be provided.");
        }

        root.getChildren().add(cursorBoundingBox);
        makeCursorColorChange();

        primaryStage.setTitle("Single Letter Display Simple");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}