package com.OneDollarHapticGloveRecogniser;

import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aau.bluetooth.cultar.glove.GloveDataPacket;
import com.aau.bluetooth.cultar.glove.android.Glove;
import com.aau.bluetooth.data.IDataPacket;
import processing.core.PApplet;

import com.aau.bluetooth.BluetoothClient;
import com.aau.bluetooth.BluetoothConnectionException;
import com.aau.bluetooth.BluetoothException;
import com.aau.bluetooth.BluetoothReadyState;
import com.aau.bluetooth.BluetoothSocketException;
import com.aau.bluetooth.IBluetooth;
import com.aau.bluetooth.android.BluetoothAndroid;
import com.aau.bluetooth.cultar.glove.GloveCmdPacket;

import java.util.Stack;

import static com.aau.bluetooth.BluetoothReadyState.INIT;

public class MainActivity extends PApplet {
    private static String TAG = "MainActivity";
    private static final String GLOVE_BT_NAME = "RNBT-2480";

    private IBluetooth mBluetooth;
    private BluetoothClient mGloveConnection;
    private Glove mGlove;
    private Handler mMainThreadHandler;

    private GloveDataPacket gloveData;
    private BluetoothReadyState connectReadyState;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // the following line must be commented or removed:
        //setContentView(R.layout.activity_main);

        mBluetooth = new BluetoothAndroid(this);
        mGlove = new Glove();
        try {
            mGlove.init(mBluetooth, GLOVE_BT_NAME);
        }
        catch (BluetoothException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        mMainThreadHandler = new Handler(Looper.getMainLooper()) {
            private IDataPacket mData;
            private BluetoothException mError;
            private BluetoothReadyState mReadyState;

            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                    case BluetoothAndroid.APPLICATION_DATA_RECEIVED:
                        mData = (IDataPacket)inputMessage.obj;
                        //Log.i(TAG, mData.toString());

                        // Just set the string on the appropriate UI text field
                        if (mData instanceof GloveDataPacket) {
//                            mTextGloveData = (mData.toString());
                            gloveData = (GloveDataPacket) mData;
                        }
                        break;

                    case BluetoothAndroid.BLUETOOTH_THREAD_READY_STATE:
                        mReadyState = (BluetoothReadyState)inputMessage.obj;
                        connectReadyState = mReadyState;
                        Log.i(TAG, mReadyState.toString());

                        switch (mReadyState) {
                            case CONNECTING:
                                connectButtonText = ("Connecting.. (" +
                                        mGloveConnection.getConnectionAttempts() +
                                        "/" +
                                        mGloveConnection.getConnectionRetries() +
                                        ")");
//                                mTextError = ("");
                                break;
                            default:
                                connectButtonText = (mReadyState.toString());

                        }
                        break;

                    case BluetoothAndroid.BLUETOOTH_THREAD_ERROR:
                        mError = (BluetoothException)inputMessage.obj;
                        Log.i(TAG, mError.getMessage());

                        if (mError instanceof BluetoothConnectionException) {
//                            mTextError = ("Connection Exception: " + mError.getMessage());
                        }
                        else if (mError instanceof BluetoothSocketException) {
//                            mTextError = ("Socket Exception: " + mError.getMessage());
                        }
                        break;

                    default:
                        super.handleMessage(inputMessage);
                }
            }
        };

        mGlove.setUiHandler(mMainThreadHandler);
        mGlove.setPosition(0, 0, 0);
        /*
        mGlove.setTrackingMode(TrackingMode.TRACKING_PROXIMITY);
        mGlove.setDirectionHitThreshold(20.0);
        mGlove.setProximityThreshold1(20.0);
        mGlove.setProximityThreshold2(30.0);
        mGlove.setProximityThreshold3(40.0);
        */
        mGloveConnection = mGlove.getBluetoothClient();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void settings() {
        size(1920, 1080, OPENGL);
        fullScreen();
    }





    // Recognizer class constants
    private int NumTemplates = 16;
    private int NumUserTamplates = 0;
    private int NumPoints = 64;
    private float SquareSize = 250.0f;
    private float HalfDiagonal = 0.5f * sqrt(250.0f * 250.0f + 250.0f * 250.0f);
    private float AngleRange = 45.0f;
    private float AnglePrecision = 2.0f;
    private float Infinity = 1e9f;
    private float Phi = 0.5f * (-1.0f + sqrt(5.0f)); // Golden Ratio
    private Recognizer recognizer;
    private Recorder recorder;
    private String resultString;
    private Result previousResult;

    // connect box variables
    private String connectButtonText;
    private int connectBoxX;
    private int connectBoxY;
    private int connectBoxHeight;
    private int connectBoxWidth;
    private int connectBoxTLAnchor;

    // calibrate box variables
    private boolean showCalibOptions;
    private String caliBoxText;
    private int caliBoxX;
    private int caliBoxY;
    private int caliBoxWidth;
    private int caliBoxHeight;
    private int calibOptionsBoxWidth;
    private int calibOptionsBoxHeight;
    private int calibOptionsBoxSpacer;
    private int resetR, resetG, resetB;
    private boolean resetPositionOnActivation;
    private boolean positionAlreadyReset;

    // glove position and position variables
    private float[] gloveDrawPointsX;
    private float[] gloveDrawPointsY;
//    long timeOfLastPoint;
    private double[] glovePositionXarray;
    private double[] glovePositionYarray;
    private int gloveArrayIndex;
    private float glovePositionX;
    private float glovePositionY;
    private boolean glovePulsed;
    private int glovePositionRelativeX;
    private int glovePositionRelativeY;
    private int gloveArraySize;

    // template adding button
    private int templBTNAnchorX, templBTNAnchorY;
    private int templBTNSize;
    private int templBTNcolorR, templBTNcolorG, templBTNcolorB;
    private int templateAddition;
    private String templateBTNtext;
    private String resulutStringDefault;
    private Point[] previousPoints;
    private boolean drawConfirmaionBTN;
    private String confirmationBTNtext;

    // user 'profile' variables
    private int finger0Threshold, finger1Threshold, finger2Threshold;
    private int gloveActivationThresholdRange;

    @Override
    public void setup() {
        recognizer = new Recognizer();
        recorder = new Recorder();
        connectButtonText = "Connect glove";
        caliBoxText = "Options";
        connectReadyState = INIT;
        gloveDrawPointsX = new float[0];
        gloveDrawPointsY = new float[0];
        glovePositionX = glovePositionY = 0f;
        templBTNcolorR = templBTNcolorG = templBTNcolorB = 200;
        templateAddition = 0;
        resulutStringDefault = "Make a gesture!\nPress NEW to add new gesture.";
        resultString = resulutStringDefault;
        templateBTNtext = "NEW";
        drawConfirmaionBTN = false;
        showCalibOptions = false;
        resetPositionOnActivation = false;
        positionAlreadyReset = false;

        gloveArraySize = 3;
        glovePositionXarray = new double[gloveArraySize];
        glovePositionYarray = new double[gloveArraySize];
        gloveArrayIndex = 0;
        glovePulsed = false;
        glovePositionRelativeX = displayWidth / 2;
        glovePositionRelativeY = displayHeight / 2;
        resetR = resetG = resetB = 200;

        finger0Threshold = finger1Threshold = finger2Threshold = 100;
        gloveActivationThresholdRange = 70;
        smooth();
    }

    @Override
    public void draw() {
        background(204);

        setVariables();
        updateGlovePosition();

        if (showCalibOptions) {
            drawCalibrationOptions();
        } else {
            drawPointer();
            drawTemplates(recognizer.getTemplates());
            drawAddTemplateButton();
            drawResultString();

            if (drawConfirmaionBTN) drawGestureAddConfirmationBTN();

            recorder.update();
            recorder.draw();

            if (recorder.hasPoints) {
                previousPoints = recorder.points;
                previousResult = recognizer.Recognize(previousPoints);
                recorder.hasPoints = false;
                if (templateAddition == 1) {
                    askToAddToExistingTemplate(previousResult);
                    resetTemplateAddition();
                }
                else if (previousResult != null) {
                    resultString = previousResult.Name + " " + str(previousResult.Ratio);
                    resetTemplateAddition();
                } else {
                    resultString = "Did not recognize!\nPress ADD to add to templates.";
                    templateBTNtext = "ADD";
                    templateAddition = 2;
                    templBTNcolorB = 230;
                }
            }

        }
        drawGloveTextData();
        drawConnectButton();
        drawCalibrateBox();
    }

    // set static variables
    private void setVariables() {
        connectBoxY = displayHeight-150;
        connectBoxHeight = 200;
        connectBoxWidth = displayWidth - 300;
        connectBoxX = (connectBoxWidth/2)+10;
        connectBoxTLAnchor = displayHeight-250;

        caliBoxX = connectBoxWidth+20+140;
        caliBoxY = connectBoxY;
        caliBoxWidth = displayWidth-(connectBoxWidth+30);
        caliBoxHeight = connectBoxHeight;

        templBTNSize = 200;
        templBTNAnchorX = displayWidth-10-templBTNSize;
        templBTNAnchorY = 10;
    }


    // draw ui elements
    private void drawResultString() {
        textSize(50);
        textAlign(LEFT, TOP);
        fill(color(12, 12, 12));
        text(resultString, 10, 20);
    }
    private void drawTemplates(Template[] templates) {
        int templatesListBAnchor = connectBoxTLAnchor - 20;
        int templatesListRAnchor = displayWidth -10 ;
        textAlign(RIGHT);
        textSize(30);
        int spacer = 35;
        for ( int i = 0; i < templates.length; i++) {
            text(templates[i].Name, templatesListRAnchor, templatesListBAnchor - (i * spacer));
        }
    }
    private void drawAddTemplateButton(){
        stroke(0);
        fill(templBTNcolorR, templBTNcolorG, templBTNcolorB);
        rect(templBTNAnchorX, templBTNAnchorY, templBTNSize, templBTNSize, 10);
        fill(0);
        textAlign(CENTER, CENTER);
        textSize(50);
        text(templateBTNtext, templBTNAnchorX+templBTNSize/2, templBTNAnchorY+templBTNSize/2);
    }
    private void drawGestureAddConfirmationBTN() {
        stroke(0);
        fill(230, 200, 200);
        rect(templBTNAnchorX, templBTNAnchorY + 10 + templBTNSize, templBTNSize, templBTNSize);
        textSize(40);
        fill(0);
        textAlign(CENTER, CENTER);
        text(confirmationBTNtext, (templBTNAnchorX + templBTNSize/2), (templBTNAnchorY + templBTNSize + (templBTNSize/2)));
    }
    private void drawPointer() {
        if (gloveData != null) {
            fill(0);

            ellipse(glovePositionX, glovePositionY, 10f, 10f);
//            textAlign(LEFT);
//            if (gloveData.finger0 <= 3 && gloveData.finger1 >= 3 && gloveData.finger2 >= 2 && System.currentTimeMillis() - timeOfLastPoint > 100) {
//                gloveDrawPointsX = addPointToList(gloveDrawPointsX, posX);
//                gloveDrawPointsY = addPointToList(gloveDrawPointsY, posY);
//                text("Adding point to list: " + posX + " " +posY, 10, connectBoxTLAnchor-280);
//                timeOfLastPoint = System.currentTimeMillis();
//            } else if (System.currentTimeMillis() - timeOfLastPoint > 2000) {
//                text("Clearing point list", 10, connectBoxTLAnchor-280);
//                gloveDrawPointsX = new float[0];
//                gloveDrawPointsY = new float[0];
//            }
//            drawGlovePoints();
        }
    }
    private void drawGloveTextData() {
        textAlign(LEFT);
        textSize(30);

        String finger0 = " Thumb: ";
        String finger1 = " Index: ";
        String finger2 = "Middle: " ;
        String heading = "Heading: ";
        String roll = "Roll: ";
        String pitch = "Pitch: ";

        int f0d = 0;
        int f1d = 0;
        int f2d = 0;

        if (gloveData != null) {
            f0d = gloveData.finger0;
            f1d = gloveData.finger1;
            f2d = gloveData.finger2;
//            finger0 = finger0 + gloveData.finger0;
//            finger1 = finger1 + gloveData.finger1;
//            finger2 = finger2 + gloveData.finger2;
            heading = heading + gloveData.heading;
            roll = roll + gloveData.roll;
            pitch = pitch + gloveData.pitch;
        }
        stroke(0);
        fill(200);
        int readingWidth = 3;
        int readingBoxWidth = readingWidth * 255;
        rect(130, connectBoxTLAnchor-245, readingBoxWidth, 30);
        rect(130, connectBoxTLAnchor-205, readingBoxWidth, 30);
        rect(130, connectBoxTLAnchor-165, readingBoxWidth, 30);
        fill(120, 120, 200);
        rect(130, connectBoxTLAnchor-245, readingWidth * abs(f0d), 30);
        rect(130, connectBoxTLAnchor-205, readingWidth * abs(f1d), 30);
        rect(130, connectBoxTLAnchor-165, readingWidth * abs(f2d), 30);
        fill(200, 120, 120, 70);
        int plusminus = gloveActivationThresholdRange/2;
        int thresholdBoxWidth = readingWidth * gloveActivationThresholdRange;
        rect(130 + readingWidth * (finger0Threshold - plusminus), connectBoxTLAnchor-245, thresholdBoxWidth, 30);
        rect(130 + readingWidth * (finger1Threshold - plusminus), connectBoxTLAnchor-205, thresholdBoxWidth, 30);
        rect(130 + readingWidth * (finger2Threshold - plusminus), connectBoxTLAnchor-165, thresholdBoxWidth, 30);

        fill(0);
        text(finger0, 10, connectBoxTLAnchor-220);
        text(finger1, 10, connectBoxTLAnchor-180);
        text(finger2, 10, connectBoxTLAnchor-140);
        text(heading, 10, connectBoxTLAnchor-100);
        text(pitch, 10, connectBoxTLAnchor-60);
        text(roll, 10, connectBoxTLAnchor-20);
    }
    private void drawCalibrateBox() {
        fill(200, 200, 200);
        stroke(141);
        rect(connectBoxWidth+20, displayHeight-250, caliBoxWidth, caliBoxHeight, 10);
        textAlign(CENTER, CENTER);
        fill(0);
        textSize(50);
        text(caliBoxText, caliBoxX, caliBoxY);

    }
    private void drawCalibrationOptions() {
        calibOptionsBoxWidth = displayWidth/3 - 20;
        calibOptionsBoxHeight = 200;
        calibOptionsBoxSpacer = 10+calibOptionsBoxHeight;

        fill(200);
        rect(calibOptionsBoxWidth + 30, calibOptionsBoxWidth, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);
        rect(calibOptionsBoxWidth + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);
        rect(calibOptionsBoxWidth + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer * 2, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);
        rect(calibOptionsBoxWidth + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer * 3, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);
        fill(resetR, resetG, resetB);
        rect(calibOptionsBoxWidth + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer * 4, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);

        fill(200);
        rect(20, calibOptionsBoxWidth, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);
        rect(20, calibOptionsBoxWidth + calibOptionsBoxSpacer * 2, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);
        rect(calibOptionsBoxWidth*2 + 40, calibOptionsBoxWidth, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);
        rect(calibOptionsBoxWidth*2 + 40, calibOptionsBoxWidth+calibOptionsBoxSpacer * 2, calibOptionsBoxWidth, calibOptionsBoxHeight, 10);

        if (mGloveConnection.isConnected()) {
            fill(0);
        } else {
            fill(220);
        }
        textAlign(CENTER, CENTER);
        textSize(50);
        text("Magnetometer", calibOptionsBoxWidth + calibOptionsBoxWidth/2 + 30, calibOptionsBoxWidth +calibOptionsBoxHeight/2);
        text("Accelero-\nmeter", calibOptionsBoxWidth + calibOptionsBoxWidth/2 + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer + calibOptionsBoxHeight/2);
        text("Hand\nposition", calibOptionsBoxWidth + calibOptionsBoxWidth/2 + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer*2 + calibOptionsBoxHeight/2);
        text("Activation\nposition", calibOptionsBoxWidth + calibOptionsBoxWidth/2 + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer * 3 + calibOptionsBoxHeight/2);
        text("Reset posi-\ntion on act.", calibOptionsBoxWidth + calibOptionsBoxWidth/2 + 30, calibOptionsBoxWidth + calibOptionsBoxSpacer * 4 + calibOptionsBoxHeight/2);

        text("Glove track\nsensitivity:\n" + gloveArraySize + " nit", 20 + calibOptionsBoxWidth/2, calibOptionsBoxWidth + calibOptionsBoxSpacer + calibOptionsBoxHeight/2);
        text("Activation\nthreshold:\n" + gloveActivationThresholdRange + " nit", 40 + calibOptionsBoxWidth * 2 + calibOptionsBoxWidth/2, calibOptionsBoxWidth + calibOptionsBoxSpacer + calibOptionsBoxHeight/2);
        textSize(90);
        text("+", 20 + calibOptionsBoxWidth/2, calibOptionsBoxWidth + calibOptionsBoxHeight/2);
        text("-", 20 + calibOptionsBoxWidth/2, calibOptionsBoxWidth * 2 + calibOptionsBoxWidth/2);
        text("+", 40 + calibOptionsBoxWidth * 2 + calibOptionsBoxWidth/2, calibOptionsBoxWidth + calibOptionsBoxHeight/2);
        text("-", 40 + calibOptionsBoxWidth * 2 + calibOptionsBoxWidth/2, calibOptionsBoxWidth * 2 + calibOptionsBoxWidth/2);
    }
    private void drawConnectButton(){
        int boxFillR;
        int boxFillG;
        int boxFillB;
        switch (connectReadyState) {
            case CONNECTING:
                boxFillR = 200;
                boxFillG = 200;
                boxFillB = 50;
                break;
            case CONNECTED:
                boxFillR = 50;
                boxFillG = 200;
                boxFillB = 50;
                break;
            case CONNECTION_FAILED:
                boxFillR = 200;
                boxFillG = 50;
                boxFillB = 50;
                break;
            case CLOSED:
                boxFillR = 200;
                boxFillG = 200;
                boxFillB = 200;
                break;
            default:
                boxFillR = 200;
                boxFillG = 200;
                boxFillB = 200;
        }
        fill(boxFillR, boxFillG, boxFillB);
        stroke(141);
        rect(10, connectBoxTLAnchor, connectBoxWidth, connectBoxHeight, 10);
        textAlign(CENTER, CENTER);
        fill(0);
        textSize(50);
        text(connectButtonText, connectBoxX, connectBoxY);
    }


    // for handling new templates
    private void askToAddToExistingTemplate(Result result) {
        if (result != null) {
            confirmationBTNtext = "Add to\nexisting";
        } else {
            confirmationBTNtext = "OK";
        }
        drawConfirmaionBTN = true;
    }
    private void confirmAddToExistingTemplate() {
        if (previousResult != null) {
            recognizer.AddTemplate(previousResult.Name, previousPoints);
        } else {
            recognizer.AddTemplate("New"+(NumUserTamplates++), previousPoints);
        }
        drawConfirmaionBTN = false;
    }
    private void resetTemplateAddition() {
        templateAddition = 0;
        templateBTNtext = "NEW";
        templBTNcolorR = 200;
        templBTNcolorG = 200;
        templBTNcolorB = 200;
    }
    private void addNewTemplateSwitcher(){
        switch (templateAddition) {
            case 0:
                templBTNcolorR = 200;
                templBTNcolorG = 240;
                templBTNcolorB = 200;
                templateAddition = 1;
                templateBTNtext = "CANCEL";
                break;
            case 1:
                resetTemplateAddition();
                break;
            case 2:
                resetTemplateAddition();
                resultString = resulutStringDefault;
                recognizer.AddTemplate("User"+(NumUserTamplates++), previousPoints);
                break;
            default:
                resetTemplateAddition();
                break;
        }
    }


    // button actions
    private void connectGlove() {
        if (!mGloveConnection.isConnected()) {
            mGloveConnection.connect();
        } else {
            mGloveConnection.close();
        }
    }
    private void calibrateGloveMagnetometer() {
        if (mGloveConnection.isConnected()) {
            GloveCmdPacket cmdPacket = new GloveCmdPacket();
            mGloveConnection.write(cmdPacket
                    .calibrateMagnetometer()
                    .getData());
        }
    }
    private void calibrateGloveAccelerometer() {
        if (mGloveConnection.isConnected()) {
            GloveCmdPacket cmdPacket = new GloveCmdPacket();
            mGloveConnection.write(cmdPacket
                    .calibrateAccelerometer()
                    .getData());
        }
    }
    private void calibrateHandPosition() {
        int desiredX = displayWidth/2;
        int desiredY = displayHeight/2;
        glovePositionRelativeX = desiredX - (int) (glovePositionX - glovePositionRelativeX);
        glovePositionRelativeY = desiredY - (int) (glovePositionY - glovePositionRelativeY);
    }
    private void calibrateActivationPosition() {
        finger0Threshold = gloveData.finger0;
        finger1Threshold = gloveData.finger1;
        finger2Threshold = gloveData.finger2;
    }
    private void checkCalibOptionsBoxActivation() {
        if (mouseX > calibOptionsBoxWidth && mouseX < calibOptionsBoxWidth*2) {
            textAlign(LEFT);
            textSize(30);
            if (mouseY > calibOptionsBoxWidth && mouseY < calibOptionsBoxWidth + calibOptionsBoxHeight){
                calibrateGloveMagnetometer();
            } else if (mouseY > calibOptionsBoxWidth + calibOptionsBoxSpacer && mouseY < calibOptionsBoxWidth + calibOptionsBoxSpacer + calibOptionsBoxHeight) {
                calibrateGloveAccelerometer();
            } else if (mouseY > calibOptionsBoxWidth + calibOptionsBoxSpacer * 2 && mouseY < calibOptionsBoxWidth + calibOptionsBoxSpacer * 2 + calibOptionsBoxHeight) {
                calibrateHandPosition();
            } else if (mouseY > calibOptionsBoxWidth + calibOptionsBoxSpacer * 3 && mouseY < calibOptionsBoxWidth + calibOptionsBoxSpacer * 3 +calibOptionsBoxHeight) {
                calibrateActivationPosition();
            } else if (mouseY > calibOptionsBoxWidth + calibOptionsBoxSpacer * 4 && mouseY < calibOptionsBoxWidth + calibOptionsBoxSpacer * 4 +calibOptionsBoxHeight) {
                if (!resetPositionOnActivation) {
                    resetR = resetB = 150;
                    resetG = 200;
                    resetPositionOnActivation = true;
                } else {
                    resetR = resetG = resetB = 200;
                    resetPositionOnActivation = false;
                }

            }
        } else if (mouseX > 20 && mouseX < 20 + calibOptionsBoxWidth) {
            if (mouseY > calibOptionsBoxWidth && mouseY < calibOptionsBoxWidth + calibOptionsBoxHeight) {
                changeGloveTrackSensitivity(1);
            } else if (mouseY > calibOptionsBoxWidth + calibOptionsBoxSpacer * 2 && mouseY < calibOptionsBoxWidth + calibOptionsBoxSpacer * 2 + calibOptionsBoxHeight) {
                changeGloveTrackSensitivity(-1);
            }
        } else if (mouseX > 40 + calibOptionsBoxWidth * 2 + calibOptionsBoxWidth/2 && mouseX < 40 + calibOptionsBoxWidth * 2 + calibOptionsBoxWidth/2 + calibOptionsBoxWidth) {
            if (mouseY > calibOptionsBoxWidth && mouseY < calibOptionsBoxWidth + calibOptionsBoxHeight) {
                changeActivationThresholdRange(10);
            } else if (mouseY > calibOptionsBoxWidth + calibOptionsBoxSpacer * 2 && mouseY < calibOptionsBoxWidth + calibOptionsBoxSpacer * 2 + calibOptionsBoxHeight) {
                changeActivationThresholdRange(-10);
            }
        }
    }


    // glove actions
    private void pulseGlove() {
        GloveCmdPacket cmdPacket = new GloveCmdPacket();
        mGloveConnection.write(cmdPacket
                .setCmd(GloveCmdPacket.CMD_VIBRATE)
                .setVib6(5)
                .setVib7(5)
                .setVib8(5)
                .getData());
    }
    private void stopGlovePulse() {
        GloveCmdPacket cmdPacket = new GloveCmdPacket();
        mGloveConnection.write(cmdPacket
                .setCmd(GloveCmdPacket.CMD_VIBRATE)
                .setVib6(0)
                .setVib7(0)
                .setVib8(0)
                .getData());
    }
    private void updateGlovePosition() {
        float modifier = 20f;
        if (gloveData != null) {
            double gloveScaledMovementX = (gloveData.roll * -1.0 * modifier);
            double gloveScaledMovementY = (gloveData.pitch * -1.8 * modifier);
            glovePositionXarray[gloveArrayIndex] = gloveScaledMovementX;
            glovePositionYarray[gloveArrayIndex] = gloveScaledMovementY;
            if (gloveArrayIndex >= gloveArraySize-1) {
                gloveArrayIndex = 0;
            } else {
                gloveArrayIndex++;
            }

            double posX = 0;
            double posY = 0;
            for (int i = 0; i<gloveArraySize;i++) {
                posX+=glovePositionXarray[i];
                posY+=glovePositionYarray[i];
            }
            posX /= gloveArraySize;
            posY /= gloveArraySize;

            glovePositionX = (float) posX + (float) glovePositionRelativeX;
            glovePositionY = (float) posY + (float) glovePositionRelativeY;
        }
    }
    private void changeGloveTrackSensitivity(int direction) {
        if (gloveArraySize + direction > 0) {
            gloveArraySize += direction;
            gloveArrayIndex = 0;
            glovePositionYarray = new double[gloveArraySize];
            glovePositionXarray = new double[gloveArraySize];
        }
    }
    private void changeActivationThresholdRange(int direction) {
        if (gloveActivationThresholdRange + direction > 0) {
            gloveActivationThresholdRange += direction;
        }
    }
    private boolean gloveActivated() {
        int plusminus = gloveActivationThresholdRange/2;
        if (gloveData != null &&
                gloveData.finger0 >= finger0Threshold - plusminus && gloveData.finger0 <= finger0Threshold + plusminus &&
                gloveData.finger1 >= finger1Threshold - plusminus && gloveData.finger1 <= finger1Threshold + plusminus &&
                gloveData.finger2 >= finger2Threshold - plusminus && gloveData.finger2 <= finger2Threshold + plusminus) {
            if (!glovePulsed) {
                glovePulsed = true;
                pulseGlove();
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                stopGlovePulse();
                            }
                        }, 500 );
            }
            if (resetPositionOnActivation && !positionAlreadyReset) {
                positionAlreadyReset = true;
                calibrateHandPosition();
            }
            return true;
        } else {
            positionAlreadyReset = false;
            glovePulsed = false;
            return false;
        }
    }

    // handle mouse presses
    @Override
    public void mousePressed() {
        if (!showCalibOptions) {
            if (mouseX > templBTNAnchorX &&
                    mouseX < templBTNAnchorX+templBTNSize &&
                    mouseY > templBTNAnchorY &&
                    mouseY < templBTNAnchorY+templBTNSize) {
                addNewTemplateSwitcher();
            } else if (templateAddition == 2) resetTemplateAddition();
            if (drawConfirmaionBTN) {
                if (mouseX > templBTNAnchorX &&
                        mouseX < templBTNAnchorX + templBTNSize &&
                        mouseY > templBTNAnchorY + 10 + templBTNSize &&
                        mouseY < templBTNAnchorY + 10 + templBTNSize*2) {
                    confirmAddToExistingTemplate();
                } else {
                    drawConfirmaionBTN = false;
                }
            }
        } else {
            if ( mGloveConnection.isConnected()) {
                checkCalibOptionsBoxActivation();
            }
        }
        if (mouseX > connectBoxX-(connectBoxWidth/2) &&
                mouseX < connectBoxX+(connectBoxWidth/2) &&
                mouseY > connectBoxY-(connectBoxHeight/2) &&
                mouseY < connectBoxY+(connectBoxHeight/2)) {
            connectGlove();
        }
        if (mouseX > caliBoxX-(caliBoxWidth/2) &&
                mouseX < caliBoxX+(caliBoxWidth/2) &&
                mouseY > caliBoxY-(caliBoxHeight/2) &&
                mouseY < caliBoxY+(caliBoxHeight/2)) {
            showCalibOptions = !showCalibOptions;
        }
    }

    // Recorder for recording touch points from screen
    private class Recorder {
        Point[] points;
        boolean recording;

        boolean hasPoints;

        Recorder() {
            points = new Point[0];
            recording = false;
        }

        void update() {
            if (recording) {
                if (mousePressed) {
                    points = (Point[]) append(points, new Point(mouseX, mouseY));
                } else if (gloveActivated()) {
                    points = (Point[]) append(points, new Point(glovePositionX, glovePositionY));
                } else {
                    recording = false;
                    if (points.length > 5) {
                        hasPoints = true;
                    }
                }
            } else {
                if (mousePressed || gloveActivated()) {
                    points = new Point[0];
                    recording = true;
                    hasPoints = false;
                }
            }
        }
        void draw() {
            int R = 0;
            int G = 0;
            int B = 0;
            if (recording) {
                R = 255;
                G = 100;
            }
            if (points.length > 1) {
                for (int i = 1; i < points.length; i++) {
                    stroke(R, G, B);
                    line(points[i - 1].X, points[i - 1].Y,
                            points[i].X, points[i].Y);
                }
            }
        }

    }

    // OneDollar implementation
    private class Point {
        float X;
        float Y;

        Point(float x, float y) {
            X = x;
            Y = y;
        }

        float distance(Point other) {
            return dist(X, Y, other.X, other.Y);
        }
    }

    private class Rectangle {
        float X;
        float Y;
        float Width;
        float Height;

        Rectangle(float x, float y, float width, float height) {
            X = x;
            Y = y;
            Width = width;
            Height = height;
        }
    }

    private class Template {
        String Name;
        Point[] Points;

        Template(String name, Point[] points) {
            Name = name;
            Points = Resample(points, NumPoints);
            Points = RotateToZero(Points);
            Points = ScaleToSquare(Points, SquareSize);
            Points = TranslateToOrigin(Points);
        }
    }

    private class Result {
        String Name;
        float Score;
        float Ratio;

        Result(String name, float score, float ratio) {
            Name = name;
            Score = score;
            Ratio = ratio;
        }
    }

    private class Recognizer {
        Template[] Templates = {};

        Recognizer() {

            // These predefines come from the sample code, and can be replaced or revised.
            // it is left as an exercise for the reader to implement a file format for reading
            // and saving templates.

            // triangle
            Point[] point0 = {new Point(137, 139), new Point(135, 141), new Point(133, 144), new Point(132, 146),
                    new Point(130, 149), new Point(128, 151), new Point(126, 155), new Point(123, 160),
                    new Point(120, 166), new Point(116, 171), new Point(112, 177), new Point(107, 183),
                    new Point(102, 188), new Point(100, 191), new Point(95, 195), new Point(90, 199),
                    new Point(86, 203), new Point(82, 206), new Point(80, 209), new Point(75, 213),
                    new Point(73, 213), new Point(70, 216), new Point(67, 219), new Point(64, 221),
                    new Point(61, 223), new Point(60, 225), new Point(62, 226), new Point(65, 225),
                    new Point(67, 226), new Point(74, 226), new Point(77, 227), new Point(85, 229),
                    new Point(91, 230), new Point(99, 231), new Point(108, 232), new Point(116, 233),
                    new Point(125, 233), new Point(134, 234), new Point(145, 233), new Point(153, 232),
                    new Point(160, 233), new Point(170, 234), new Point(177, 235), new Point(179, 236),
                    new Point(186, 237), new Point(193, 238), new Point(198, 239), new Point(200, 237),
                    new Point(202, 239), new Point(204, 238), new Point(206, 234), new Point(205, 230),
                    new Point(202, 222), new Point(197, 216), new Point(192, 207), new Point(186, 198),
                    new Point(179, 189), new Point(174, 183), new Point(170, 178), new Point(164, 171),
                    new Point(161, 168), new Point(154, 160), new Point(148, 155), new Point(143, 150),
                    new Point(138, 148), new Point(136, 148)};
            AddTemplate("triangle", point0);

            // x
            Point[] point1 = {new Point(87, 142), new Point(89, 145), new Point(91, 148), new Point(93, 151),
                    new Point(96, 155), new Point(98, 157), new Point(100, 160), new Point(102, 162),
                    new Point(106, 167), new Point(108, 169), new Point(110, 171), new Point(115, 177),
                    new Point(119, 183), new Point(123, 189), new Point(127, 193), new Point(129, 196),
                    new Point(133, 200), new Point(137, 206), new Point(140, 209), new Point(143, 212),
                    new Point(146, 215), new Point(151, 220), new Point(153, 222), new Point(155, 223),
                    new Point(157, 225), new Point(158, 223), new Point(157, 218), new Point(155, 211),
                    new Point(154, 208), new Point(152, 200), new Point(150, 189), new Point(148, 179),
                    new Point(147, 170), new Point(147, 158), new Point(147, 148), new Point(147, 141),
                    new Point(147, 136), new Point(144, 135), new Point(142, 137), new Point(140, 139),
                    new Point(135, 145), new Point(131, 152), new Point(124, 163), new Point(116, 177),
                    new Point(108, 191), new Point(100, 206), new Point(94, 217), new Point(91, 222),
                    new Point(89, 225), new Point(87, 226), new Point(87, 224)};
            AddTemplate("x", point1);

            // rectangle
            Point[] point2 = {new Point(78, 149), new Point(78, 153), new Point(78, 157), new Point(78, 160),
                    new Point(79, 162), new Point(79, 164), new Point(79, 167), new Point(79, 169),
                    new Point(79, 173), new Point(79, 178), new Point(79, 183), new Point(80, 189),
                    new Point(80, 193), new Point(80, 198), new Point(80, 202), new Point(81, 208),
                    new Point(81, 210), new Point(81, 216), new Point(82, 222), new Point(82, 224),
                    new Point(82, 227), new Point(83, 229), new Point(83, 231), new Point(85, 230),
                    new Point(88, 232), new Point(90, 233), new Point(92, 232), new Point(94, 233),
                    new Point(99, 232), new Point(102, 233), new Point(106, 233), new Point(109, 234),
                    new Point(117, 235), new Point(123, 236), new Point(126, 236), new Point(135, 237),
                    new Point(142, 238), new Point(145, 238), new Point(152, 238), new Point(154, 239),
                    new Point(165, 238), new Point(174, 237), new Point(179, 236), new Point(186, 235),
                    new Point(191, 235), new Point(195, 233), new Point(197, 233), new Point(200, 233),
                    new Point(201, 235), new Point(201, 233), new Point(199, 231), new Point(198, 226),
                    new Point(198, 220), new Point(196, 207), new Point(195, 195), new Point(195, 181),
                    new Point(195, 173), new Point(195, 163), new Point(194, 155), new Point(192, 145),
                    new Point(192, 143), new Point(192, 138), new Point(191, 135), new Point(191, 133),
                    new Point(191, 130), new Point(190, 128), new Point(188, 129), new Point(186, 129),
                    new Point(181, 132), new Point(173, 131), new Point(162, 131), new Point(151, 132),
                    new Point(149, 132), new Point(138, 132), new Point(136, 132), new Point(122, 131),
                    new Point(120, 131), new Point(109, 130), new Point(107, 130), new Point(90, 132),
                    new Point(81, 133), new Point(76, 133)};
            AddTemplate("rectangle", point2);

            // circle
            Point[] point3 = {new Point(127, 141), new Point(124, 140), new Point(120, 139), new Point(118, 139),
                    new Point(116, 139), new Point(111, 140), new Point(109, 141), new Point(104, 144),
                    new Point(100, 147), new Point(96, 152), new Point(93, 157), new Point(90, 163),
                    new Point(87, 169), new Point(85, 175), new Point(83, 181), new Point(82, 190),
                    new Point(82, 195), new Point(83, 200), new Point(84, 205), new Point(88, 213),
                    new Point(91, 216), new Point(96, 219), new Point(103, 222), new Point(108, 224),
                    new Point(111, 224), new Point(120, 224), new Point(133, 223), new Point(142, 222),
                    new Point(152, 218), new Point(160, 214), new Point(167, 210), new Point(173, 204),
                    new Point(178, 198), new Point(179, 196), new Point(182, 188), new Point(182, 177),
                    new Point(178, 167), new Point(170, 150), new Point(163, 138), new Point(152, 130),
                    new Point(143, 129), new Point(140, 131), new Point(129, 136), new Point(126, 139)};

            AddTemplate("circle", point3);

            // check
            Point[] point4 = {new Point(91, 185), new Point(93, 185), new Point(95, 185), new Point(97, 185), new Point(100, 188),
                    new Point(102, 189), new Point(104, 190), new Point(106, 193), new Point(108, 195), new Point(110, 198),
                    new Point(112, 201), new Point(114, 204), new Point(115, 207), new Point(117, 210), new Point(118, 212),
                    new Point(120, 214), new Point(121, 217), new Point(122, 219), new Point(123, 222), new Point(124, 224),
                    new Point(126, 226), new Point(127, 229), new Point(129, 231), new Point(130, 233), new Point(129, 231),
                    new Point(129, 228), new Point(129, 226), new Point(129, 224), new Point(129, 221), new Point(129, 218),
                    new Point(129, 212), new Point(129, 208), new Point(130, 198), new Point(132, 189), new Point(134, 182),
                    new Point(137, 173), new Point(143, 164), new Point(147, 157), new Point(151, 151), new Point(155, 144),
                    new Point(161, 137), new Point(165, 131), new Point(171, 122), new Point(174, 118), new Point(176, 114),
                    new Point(177, 112), new Point(177, 114), new Point(175, 116), new Point(173, 118)};
            AddTemplate("check", point4);

            // caret
            Point[] point5 = {new Point(79, 245), new Point(79, 242), new Point(79, 239), new Point(80, 237), new Point(80, 234),
                    new Point(81, 232), new Point(82, 230), new Point(84, 224), new Point(86, 220), new Point(86, 218),
                    new Point(87, 216), new Point(88, 213), new Point(90, 207), new Point(91, 202), new Point(92, 200),
                    new Point(93, 194), new Point(94, 192), new Point(96, 189), new Point(97, 186), new Point(100, 179),
                    new Point(102, 173), new Point(105, 165), new Point(107, 160), new Point(109, 158), new Point(112, 151),
                    new Point(115, 144), new Point(117, 139), new Point(119, 136), new Point(119, 134), new Point(120, 132),
                    new Point(121, 129), new Point(122, 127), new Point(124, 125), new Point(126, 124), new Point(129, 125),
                    new Point(131, 127), new Point(132, 130), new Point(136, 139), new Point(141, 154), new Point(145, 166),
                    new Point(151, 182), new Point(156, 193), new Point(157, 196), new Point(161, 209), new Point(162, 211),
                    new Point(167, 223), new Point(169, 229), new Point(170, 231), new Point(173, 237), new Point(176, 242),
                    new Point(177, 244), new Point(179, 250), new Point(181, 255), new Point(182, 257)};
            AddTemplate("caret", point5);

            // question
            Point[] point6 = {new Point(104, 145), new Point(103, 142), new Point(103, 140), new Point(103, 138), new Point(103, 135),
                    new Point(104, 133), new Point(105, 131), new Point(106, 128), new Point(107, 125), new Point(108, 123),
                    new Point(111, 121), new Point(113, 118), new Point(115, 116), new Point(117, 116), new Point(119, 116),
                    new Point(121, 115), new Point(124, 116), new Point(126, 115), new Point(128, 114), new Point(130, 115),
                    new Point(133, 116), new Point(135, 117), new Point(140, 120), new Point(142, 121), new Point(144, 123),
                    new Point(146, 125), new Point(149, 127), new Point(150, 129), new Point(152, 130), new Point(154, 132),
                    new Point(156, 134), new Point(158, 137), new Point(159, 139), new Point(160, 141), new Point(160, 143),
                    new Point(160, 146), new Point(160, 149), new Point(159, 153), new Point(158, 155), new Point(157, 157),
                    new Point(155, 159), new Point(153, 161), new Point(151, 163), new Point(146, 167), new Point(142, 170),
                    new Point(138, 172), new Point(134, 173), new Point(132, 175), new Point(127, 175), new Point(124, 175),
                    new Point(122, 176), new Point(120, 178), new Point(119, 180), new Point(119, 183), new Point(119, 185),
                    new Point(120, 190), new Point(121, 194), new Point(122, 200), new Point(123, 205), new Point(123, 211),
                    new Point(124, 215), new Point(124, 223), new Point(124, 225)};
            AddTemplate("question", point6);

            // arrow
            Point[] point7 = {new Point(68, 222), new Point(70, 220), new Point(73, 218), new Point(75, 217), new Point(77, 215),
                    new Point(80, 213), new Point(82, 212), new Point(84, 210), new Point(87, 209), new Point(89, 208), new Point(92, 206),
                    new Point(95, 204), new Point(101, 201), new Point(106, 198), new Point(112, 194), new Point(118, 191),
                    new Point(124, 187), new Point(127, 186), new Point(132, 183), new Point(138, 181), new Point(141, 180),
                    new Point(146, 178), new Point(154, 173), new Point(159, 171), new Point(161, 170), new Point(166, 167),
                    new Point(168, 167), new Point(171, 166), new Point(174, 164), new Point(177, 162), new Point(180, 160),
                    new Point(182, 158), new Point(183, 156), new Point(181, 154), new Point(178, 153), new Point(171, 153),
                    new Point(164, 153), new Point(160, 153), new Point(150, 154), new Point(147, 155), new Point(141, 157),
                    new Point(137, 158), new Point(135, 158), new Point(137, 158), new Point(140, 157), new Point(143, 156),
                    new Point(151, 154), new Point(160, 152), new Point(170, 149), new Point(179, 147), new Point(185, 145),
                    new Point(192, 144), new Point(196, 144), new Point(198, 144), new Point(200, 144), new Point(201, 147),
                    new Point(199, 149), new Point(194, 157), new Point(191, 160), new Point(186, 167), new Point(180, 176),
                    new Point(177, 179), new Point(171, 187), new Point(169, 189), new Point(165, 194), new Point(164, 196)};
            AddTemplate("arrow", point7);

            // left square bracket
            Point[] point8 = {new Point(140, 124), new Point(138, 123), new Point(135, 122), new Point(133, 123), new Point(130, 123),
                    new Point(128, 124), new Point(125, 125), new Point(122, 124), new Point(120, 124), new Point(118, 124),
                    new Point(116, 125), new Point(113, 125), new Point(111, 125), new Point(108, 124), new Point(106, 125),
                    new Point(104, 125), new Point(102, 124), new Point(100, 123), new Point(98, 123), new Point(95, 124),
                    new Point(93, 123), new Point(90, 124), new Point(88, 124), new Point(85, 125), new Point(83, 126),
                    new Point(81, 127), new Point(81, 129), new Point(82, 131), new Point(82, 134), new Point(83, 138),
                    new Point(84, 141), new Point(84, 144), new Point(85, 148), new Point(85, 151), new Point(86, 156),
                    new Point(86, 160), new Point(86, 164), new Point(86, 168), new Point(87, 171), new Point(87, 175),
                    new Point(87, 179), new Point(87, 182), new Point(87, 186), new Point(88, 188), new Point(88, 195),
                    new Point(88, 198), new Point(88, 201), new Point(88, 207), new Point(89, 211), new Point(89, 213),
                    new Point(89, 217), new Point(89, 222), new Point(88, 225), new Point(88, 229), new Point(88, 231),
                    new Point(88, 233), new Point(88, 235), new Point(89, 237), new Point(89, 240), new Point(89, 242),
                    new Point(91, 241), new Point(94, 241), new Point(96, 240), new Point(98, 239), new Point(105, 240),
                    new Point(109, 240), new Point(113, 239), new Point(116, 240), new Point(121, 239), new Point(130, 240),
                    new Point(136, 237), new Point(139, 237), new Point(144, 238), new Point(151, 237), new Point(157, 236),
                    new Point(159, 237)};
            AddTemplate("left square bracket", point8);

            // right square bracket.
            Point[] point9 = {new Point(112, 138), new Point(112, 136), new Point(115, 136), new Point(118, 137), new Point(120, 136),
                    new Point(123, 136), new Point(125, 136), new Point(128, 136), new Point(131, 136), new Point(134, 135),
                    new Point(137, 135), new Point(140, 134), new Point(143, 133), new Point(145, 132), new Point(147, 132),
                    new Point(149, 132), new Point(152, 132), new Point(153, 134), new Point(154, 137), new Point(155, 141),
                    new Point(156, 144), new Point(157, 152), new Point(158, 161), new Point(160, 170), new Point(162, 182),
                    new Point(164, 192), new Point(166, 200), new Point(167, 209), new Point(168, 214), new Point(168, 216),
                    new Point(169, 221), new Point(169, 223), new Point(169, 228), new Point(169, 231), new Point(166, 233),
                    new Point(164, 234), new Point(161, 235), new Point(155, 236), new Point(147, 235), new Point(140, 233),
                    new Point(131, 233), new Point(124, 233), new Point(117, 235), new Point(114, 238), new Point(112, 238)};
            AddTemplate("right square bracket", point9);

            // v
            Point[] point10 = {new Point(89, 164), new Point(90, 162), new Point(92, 162), new Point(94, 164), new Point(95, 166),
                    new Point(96, 169), new Point(97, 171), new Point(99, 175), new Point(101, 178), new Point(103, 182),
                    new Point(106, 189), new Point(108, 194), new Point(111, 199), new Point(114, 204), new Point(117, 209),
                    new Point(119, 214), new Point(122, 218), new Point(124, 222), new Point(126, 225), new Point(128, 228),
                    new Point(130, 229), new Point(133, 233), new Point(134, 236), new Point(136, 239), new Point(138, 240),
                    new Point(139, 242), new Point(140, 244), new Point(142, 242), new Point(142, 240), new Point(142, 237),
                    new Point(143, 235), new Point(143, 233), new Point(145, 229), new Point(146, 226), new Point(148, 217),
                    new Point(149, 208), new Point(149, 205), new Point(151, 196), new Point(151, 193), new Point(153, 182),
                    new Point(155, 172), new Point(157, 165), new Point(159, 160), new Point(162, 155), new Point(164, 150),
                    new Point(165, 148), new Point(166, 146)};
            AddTemplate("v", point10);

            // delete
            Point[] point11 = {new Point(123, 129), new Point(123, 131), new Point(124, 133), new Point(125, 136), new Point(127, 140),
                    new Point(129, 142), new Point(133, 148), new Point(137, 154), new Point(143, 158), new Point(145, 161),
                    new Point(148, 164), new Point(153, 170), new Point(158, 176), new Point(160, 178), new Point(164, 183),
                    new Point(168, 188), new Point(171, 191), new Point(175, 196), new Point(178, 200), new Point(180, 202),
                    new Point(181, 205), new Point(184, 208), new Point(186, 210), new Point(187, 213), new Point(188, 215),
                    new Point(186, 212), new Point(183, 211), new Point(177, 208), new Point(169, 206), new Point(162, 205),
                    new Point(154, 207), new Point(145, 209), new Point(137, 210), new Point(129, 214), new Point(122, 217),
                    new Point(118, 218), new Point(111, 221), new Point(109, 222), new Point(110, 219), new Point(112, 217),
                    new Point(118, 209), new Point(120, 207), new Point(128, 196), new Point(135, 187), new Point(138, 183),
                    new Point(148, 167), new Point(157, 153), new Point(163, 145), new Point(165, 142), new Point(172, 133),
                    new Point(177, 127), new Point(179, 127), new Point(180, 125)};
            AddTemplate("delete", point11);

            // left curly brace
            Point[] point12 = {new Point(150, 116), new Point(147, 117), new Point(145, 116), new Point(142, 116), new Point(139, 117),
                    new Point(136, 117), new Point(133, 118), new Point(129, 121), new Point(126, 122), new Point(123, 123),
                    new Point(120, 125), new Point(118, 127), new Point(115, 128), new Point(113, 129), new Point(112, 131),
                    new Point(113, 134), new Point(115, 134), new Point(117, 135), new Point(120, 135), new Point(123, 137),
                    new Point(126, 138), new Point(129, 140), new Point(135, 143), new Point(137, 144), new Point(139, 147),
                    new Point(141, 149), new Point(140, 152), new Point(139, 155), new Point(134, 159), new Point(131, 161),
                    new Point(124, 166), new Point(121, 166), new Point(117, 166), new Point(114, 167), new Point(112, 166),
                    new Point(114, 164), new Point(116, 163), new Point(118, 163), new Point(120, 162), new Point(122, 163),
                    new Point(125, 164), new Point(127, 165), new Point(129, 166), new Point(130, 168), new Point(129, 171),
                    new Point(127, 175), new Point(125, 179), new Point(123, 184), new Point(121, 190), new Point(120, 194),
                    new Point(119, 199), new Point(120, 202), new Point(123, 207), new Point(127, 211), new Point(133, 215),
                    new Point(142, 219), new Point(148, 220), new Point(151, 221)};
            AddTemplate("left curly brace", point12);

            // right curly brace
            Point[] point13 = {new Point(117, 132), new Point(115, 132), new Point(115, 129), new Point(117, 129), new Point(119, 128),
                    new Point(122, 127), new Point(125, 127), new Point(127, 127), new Point(130, 127), new Point(133, 129),
                    new Point(136, 129), new Point(138, 130), new Point(140, 131), new Point(143, 134), new Point(144, 136),
                    new Point(145, 139), new Point(145, 142), new Point(145, 145), new Point(145, 147), new Point(145, 149),
                    new Point(144, 152), new Point(142, 157), new Point(141, 160), new Point(139, 163), new Point(137, 166),
                    new Point(135, 167), new Point(133, 169), new Point(131, 172), new Point(128, 173), new Point(126, 176),
                    new Point(125, 178), new Point(125, 180), new Point(125, 182), new Point(126, 184), new Point(128, 187),
                    new Point(130, 187), new Point(132, 188), new Point(135, 189), new Point(140, 189), new Point(145, 189),
                    new Point(150, 187), new Point(155, 186), new Point(157, 185), new Point(159, 184), new Point(156, 185),
                    new Point(154, 185), new Point(149, 185), new Point(145, 187), new Point(141, 188), new Point(136, 191),
                    new Point(134, 191), new Point(131, 192), new Point(129, 193), new Point(129, 195), new Point(129, 197),
                    new Point(131, 200), new Point(133, 202), new Point(136, 206), new Point(139, 211), new Point(142, 215),
                    new Point(145, 220), new Point(147, 225), new Point(148, 231), new Point(147, 239), new Point(144, 244),
                    new Point(139, 248), new Point(134, 250), new Point(126, 253), new Point(119, 253), new Point(115, 253)};
            AddTemplate("right curly brace", point13);

            // star
            Point[] point14 = {new Point(75, 250), new Point(75, 247), new Point(77, 244), new Point(78, 242), new Point(79, 239),
                    new Point(80, 237), new Point(82, 234), new Point(82, 232), new Point(84, 229), new Point(85, 225),
                    new Point(87, 222), new Point(88, 219), new Point(89, 216), new Point(91, 212), new Point(92, 208),
                    new Point(94, 204), new Point(95, 201), new Point(96, 196), new Point(97, 194), new Point(98, 191),
                    new Point(100, 185), new Point(102, 178), new Point(104, 173), new Point(104, 171), new Point(105, 164),
                    new Point(106, 158), new Point(107, 156), new Point(107, 152), new Point(108, 145), new Point(109, 141),
                    new Point(110, 139), new Point(112, 133), new Point(113, 131), new Point(116, 127), new Point(117, 125),
                    new Point(119, 122), new Point(121, 121), new Point(123, 120), new Point(125, 122), new Point(125, 125),
                    new Point(127, 130), new Point(128, 133), new Point(131, 143), new Point(136, 153), new Point(140, 163),
                    new Point(144, 172), new Point(145, 175), new Point(151, 189), new Point(156, 201), new Point(161, 213),
                    new Point(166, 225), new Point(169, 233), new Point(171, 236), new Point(174, 243), new Point(177, 247),
                    new Point(178, 249), new Point(179, 251), new Point(180, 253), new Point(180, 255), new Point(179, 257),
                    new Point(177, 257), new Point(174, 255), new Point(169, 250), new Point(164, 247), new Point(160, 245),
                    new Point(149, 238), new Point(138, 230), new Point(127, 221), new Point(124, 220), new Point(112, 212),
                    new Point(110, 210), new Point(96, 201), new Point(84, 195), new Point(74, 190), new Point(64, 182),
                    new Point(55, 175), new Point(51, 172), new Point(49, 170), new Point(51, 169), new Point(56, 169),
                    new Point(66, 169), new Point(78, 168), new Point(92, 166), new Point(107, 164), new Point(123, 161),
                    new Point(140, 162), new Point(156, 162), new Point(171, 160), new Point(173, 160), new Point(186, 160),
                    new Point(195, 160), new Point(198, 161), new Point(203, 163), new Point(208, 163), new Point(206, 164),
                    new Point(200, 167), new Point(187, 172), new Point(174, 179), new Point(172, 181), new Point(153, 192),
                    new Point(137, 201), new Point(123, 211), new Point(112, 220), new Point(99, 229), new Point(90, 237),
                    new Point(80, 244), new Point(73, 250), new Point(69, 254), new Point(69, 252)};
            AddTemplate("star", point14);

            // pig tail
            Point[] point15 = {new Point(81, 219), new Point(84, 218), new Point(86, 220), new Point(88, 220), new Point(90, 220),
                    new Point(92, 219), new Point(95, 220), new Point(97, 219), new Point(99, 220), new Point(102, 218),
                    new Point(105, 217), new Point(107, 216), new Point(110, 216), new Point(113, 214), new Point(116, 212),
                    new Point(118, 210), new Point(121, 208), new Point(124, 205), new Point(126, 202), new Point(129, 199),
                    new Point(132, 196), new Point(136, 191), new Point(139, 187), new Point(142, 182), new Point(144, 179),
                    new Point(146, 174), new Point(148, 170), new Point(149, 168), new Point(151, 162), new Point(152, 160),
                    new Point(152, 157), new Point(152, 155), new Point(152, 151), new Point(152, 149), new Point(152, 146),
                    new Point(149, 142), new Point(148, 139), new Point(145, 137), new Point(141, 135), new Point(139, 135),
                    new Point(134, 136), new Point(130, 140), new Point(128, 142), new Point(126, 145), new Point(122, 150),
                    new Point(119, 158), new Point(117, 163), new Point(115, 170), new Point(114, 175), new Point(117, 184),
                    new Point(120, 190), new Point(125, 199), new Point(129, 203), new Point(133, 208), new Point(138, 213),
                    new Point(145, 215), new Point(155, 218), new Point(164, 219), new Point(166, 219), new Point(177, 219),
                    new Point(182, 218), new Point(192, 216), new Point(196, 213), new Point(199, 212), new Point(201, 211)};
            AddTemplate("pigtail", point15);
        }

        Result Recognize(Point[] points) {
            points = Resample(points, NumPoints);
            points = RotateToZero(points);
            points = ScaleToSquare(points, SquareSize);
            points = TranslateToOrigin(points);
            float best = Infinity;
            float sndBest = Infinity;
            int t = -1;
            for (int i = 0; i < Templates.length; i++) {
                float d = DistanceAtBestAngle(points, Templates[i], -AngleRange, AngleRange, AnglePrecision);
                if (d < best) {
                    sndBest = best;
                    best = d;
                    t = i;
                } else if (d < sndBest) {
                    sndBest = d;
                }
            }
            float score = 1.0f - (best / HalfDiagonal);
            float otherScore = 1.0f - (sndBest / HalfDiagonal);
            float ratio = otherScore / score;
            // The threshold of 0.7 is arbitrary, and not part of the original code.
            if (t > -1 && score > 0.7) {
                return new Result(Templates[t].Name, score, ratio);
            } else {
//                return new Result("- none - ", 0.0f, 1.0f);
                return null;
            }
        }

        int AddTemplate(String name, Point[] points) {
            Templates = (Template[]) append(Templates, new Template(name, points));
            int num = 0;
            for (int i = 0; i < Templates.length; i++) {
                if (Templates[i].Name == name) {
                    num++;
                }
            }
            return num;
        }

        Template[] getTemplates() {
            return Templates;
        }

        void DeleteUserTemplates() {
            Templates = (Template[]) subset(Templates, 0, NumTemplates);
        }

    }

    private float PathLength(Point[] points) {
        float d = 0.0f;
        for (int i = 1; i < points.length; i++) {
            d += points[i - 1].distance(points[i]);
        }
        return d;
    }

    private float PathDistance(Point[] pts1, Point[] pts2) {
        if (pts1.length != pts2.length) {
            println("Lengths differ. " + pts1.length + " != " + pts2.length);
            return Infinity;
        }
        float d = 0.0f;
        for (int i = 0; i < pts1.length; i++) {
            d += pts1[i].distance(pts2[i]);
        }
        return d / (float) pts1.length;
    }

    private Rectangle BoundingBox(Point[] points) {
        float minX = Infinity;
        float maxX = -Infinity;
        float minY = Infinity;
        float maxY = -Infinity;

        for (int i = 1; i < points.length; i++) {
            minX = min(points[i].X, minX);
            maxX = max(points[i].X, maxX);
            minY = min(points[i].Y, minY);
            maxY = max(points[i].Y, maxY);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private Point Centroid(Point[] points) {
        Point centriod = new Point(0.0f, 0.0f);
        for (int i = 1; i < points.length; i++) {
            centriod.X += points[i].X;
            centriod.Y += points[i].Y;
        }
        centriod.X /= points.length;
        centriod.Y /= points.length;
        return centriod;
    }

    private Point[] RotateBy(Point[] points, float theta) {
        Point c = Centroid(points);
        float Cos = cos(theta);
        float Sin = sin(theta);

        Point[] newpoints = {};
        for (int i = 0; i < points.length; i++) {
            float qx = (points[i].X - c.X) * Cos - (points[i].Y - c.Y) * Sin + c.X;
            float qy = (points[i].X - c.X) * Sin + (points[i].Y - c.Y) * Cos + c.Y;
            newpoints = (Point[]) append(newpoints, new Point(qx, qy));
        }
        return newpoints;
    }

    private Point[] RotateToZero(Point[] points) {
        Point c = Centroid(points);
        float theta = atan2(c.Y - points[0].Y, c.X - points[0].X);
        return RotateBy(points, -theta);
    }

    private Point[] Resample(Point[] points, int n) {
        float I = PathLength(points) / ((float) n - 1.0f);
        float D = 0.0f;
        Point[] newpoints = {};
        Stack stack = new Stack();
        for (int i = 0; i < points.length; i++) {
            stack.push(points[points.length - 1 - i]);
        }

        while (!stack.empty()) {
            Point pt1 = (Point) stack.pop();

            if (stack.empty()) {
                newpoints = (Point[]) append(newpoints, pt1);
                continue;
            }
            Point pt2 = (Point) stack.peek();
            float d = pt1.distance(pt2);
            if ((D + d) >= I) {
                float qx = pt1.X + ((I - D) / d) * (pt2.X - pt1.X);
                float qy = pt1.Y + ((I - D) / d) * (pt2.Y - pt1.Y);
                Point q = new Point(qx, qy);
                newpoints = (Point[]) append(newpoints, q);
                stack.push(q);
                D = 0.0f;
            } else {
                D += d;
            }
        }

        if (newpoints.length == (n - 1)) {
            newpoints = (Point[]) append(newpoints, points[points.length - 1]);
        }
        return newpoints;

    }

    private Point[] ScaleToSquare(Point[] points, float sz) {
        Rectangle B = BoundingBox(points);
        Point[] newpoints = {};
        for (int i = 0; i < points.length; i++) {
            float qx = points[i].X * (sz / B.Width);
            float qy = points[i].Y * (sz / B.Height);
            newpoints = (Point[]) append(newpoints, new Point(qx, qy));
        }
        return newpoints;
    }

    private float DistanceAtBestAngle(Point[] points, Template T, float a, float b, float threshold) {
        float x1 = Phi * a + (1.0f - Phi) * b;
        float f1 = DistanceAtAngle(points, T, x1);
        float x2 = (1.0f - Phi) * a + Phi * b;
        float f2 = DistanceAtAngle(points, T, x2);
        while (abs(b - a) > threshold) {
            if (f1 < f2) {
                b = x2;
                x2 = x1;
                f2 = f1;
                x1 = Phi * a + (1.0f - Phi) * b;
                f1 = DistanceAtAngle(points, T, x1);
            } else {
                a = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1.0f - Phi) * a + Phi * b;
                f2 = DistanceAtAngle(points, T, x2);
            }
        }
        return min(f1, f2);
    }

    private float DistanceAtAngle(Point[] points, Template T, float theta) {
        Point[] newpoints = RotateBy(points, theta);
        return PathDistance(newpoints, T.Points);
    }

    private Point[] TranslateToOrigin(Point[] points) {
        Point c = Centroid(points);
        Point[] newpoints = {};
        for (int i = -0; i < points.length; i++) {
            float qx = points[i].X - c.X;
            float qy = points[i].Y - c.Y;
            newpoints = (Point[]) append(newpoints, new Point(qx, qy));
        }
        return newpoints;
    }


    // unused, since they are implemented elsewhere
    public void drawGlovePoints() {
        stroke(0);
        strokeWeight(3);
        for (int i = 0; i < gloveDrawPointsX.length-1; i++) {
            line(gloveDrawPointsX[i], gloveDrawPointsY[i], gloveDrawPointsX[i+1], gloveDrawPointsY[i+1]);
        }
    }
    public float[] addPointToList(float[] list, float point) {
        float[] newList = new float[list.length+1];
        for (int i = 0; i < list.length; i++) {
            newList[i] = list[i];
        }
        newList[list.length] = point;
        return newList;
    }


}