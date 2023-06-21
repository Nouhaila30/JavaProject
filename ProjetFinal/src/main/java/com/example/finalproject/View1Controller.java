package com.example.finalproject;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class View1Controller implements Initializable {

    @FXML
    private AnchorPane FirstView;
    @FXML
    private Button CreateAccountBtn;

    @FXML
    private Button alreadyHaveAccount;

    @FXML
    private TextField fp_answer;

    @FXML
    private TextField fp_username;

    @FXML
    private Button fp_back;

    @FXML
    private Button fp_proceedBtn;

    @FXML
    private ComboBox<?> fp_question;

    @FXML
    private Button loginBtn;

    @FXML
    private Button np_back;

    @FXML
    private Button np_changePassBtn;

    @FXML
    private PasswordField np_confirmPassword;

    @FXML
    private PasswordField np_newPassword;

    @FXML
    private AnchorPane question_form;

    @FXML
    private Hyperlink si_forgotPass;

    @FXML
    private AnchorPane si_loginForm;

    @FXML
    private AnchorPane np_newPassForm;

    @FXML
    private PasswordField si_password;

    @FXML
    private TextField si_username;

    @FXML
    private AnchorPane side_form;

    @FXML
    private TextField su_answer;

    @FXML
    private PasswordField su_password;

    @FXML
    private ComboBox<?> su_question;

    @FXML
    private Button su_signupBtn;

    @FXML
    private AnchorPane su_signupForm;

    @FXML
    private TextField su_username;


    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;

    private double x=0;
    private double y=0;

    public void loginBtn(){
        if(si_username.getText().isEmpty() || si_password.getText().isEmpty()){
            alert =new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Incorrect username/password");
            alert.showAndWait();
        }else {
            String selcData="SELECT username, password FROM client WHERE username = ? and password = ?";
            connect=database.connectDB();

            try {
                prepare=connect.prepareStatement(selcData);
                prepare.setString(1,si_username.getText());
                prepare.setString(2,si_password.getText());

                result=prepare.executeQuery();

                if(result.next()){
                    alert =new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully login!");
                    alert.showAndWait();

                    //Mine
                    FirstView.getScene().getWindow().hide();

                    Parent root= FXMLLoader.load(getClass().getResource("View2.fxml"));

                    Stage stage=new Stage();
                    Scene scene=new Scene(root);

                    root.setOnMousePressed((MouseEvent event) ->{
                        x= event.getSceneX();
                        y= event.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent event) ->{
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                    });


                    stage.initStyle(StageStyle.TRANSPARENT);

                    stage.setScene(scene);
                    stage.show();

                }else {
                    alert =new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect username/password");
                    alert.showAndWait();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void regBtn(){
        if(su_username.getText().isEmpty() || su_password.getText().isEmpty()
                || su_question.getSelectionModel().getSelectedItem()==null
                || su_answer.getText().isEmpty()){
            alert =new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
        }else {
            String regData="INSERT INTO client (username,password,question,answer)"
                    +"VALUES(?,?,?,?)";
            connect=database.connectDB();
            try {

                String checkUsername="SELECT username FROM client WHERE username='"
                        +su_username.getText()+ "'";

                prepare=connect.prepareStatement(checkUsername);
                result=prepare.executeQuery();

                if (result.next()){
                    alert =new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText(su_username.getText()+" is already taken");
                    alert.showAndWait();
                }else {

                    prepare = connect.prepareStatement(regData);
                    prepare.setString(1, su_username.getText());
                    prepare.setString(2, su_password.getText());
                    prepare.setString(3, (String) su_question.getSelectionModel().getSelectedItem());
                    prepare.setString(4, su_answer.getText());

                    prepare.executeUpdate();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully registred Account!");
                    alert.showAndWait();

                    su_username.setText("");
                    su_password.setText("");
                    su_question.getSelectionModel().clearSelection();
                    su_answer.setText("");

                    TranslateTransition slider = new TranslateTransition();

                    slider.setNode(side_form);
                    slider.setToX(0);
                    slider.setDuration(Duration.seconds(.5));
                    slider.setOnFinished((ActionEvent e) -> {
                        alreadyHaveAccount.setVisible(false);
                        CreateAccountBtn.setVisible(true);
                    });

                    slider.play();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String[] questionList={"What is your favorite Color?","What is your favorite food?","What is your birth date?"};
    public void regLquestionList(){
        List<String> listQ=new ArrayList<>();
        for(String data: questionList){
            listQ.add(data);
        }
        ObservableList listData= FXCollections.observableArrayList(listQ);

        su_question.setItems(listData);
    }

    public void switchForgotPass(){
        question_form.setVisible(true);
        si_loginForm.setVisible(false);

        forgtoPassQuestionList();
    }

    public void proceedBtn(){
        if(fp_username.getText().isEmpty() || fp_question.getSelectionModel().getSelectedItem() == null
            || fp_answer.getText().isEmpty()){

            alert =new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();

        }else {

            String selectData= "SELECT username, question, answer FROM client where username = ? AND question = ? AND answer = ?";
            connect=database.connectDB();

            try {
                prepare=connect.prepareStatement(selectData);
                prepare.setString(1,fp_username.getText());
                prepare.setString(2, (String) fp_question.getSelectionModel().getSelectedItem());
                prepare.setString(3,fp_answer.getText());

                result=prepare.executeQuery();

                if (result.next()){
                    np_newPassForm.setVisible(true);
                    question_form.setVisible(false);
                }else {
                    alert =new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect Information");
                    alert.showAndWait();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void changPassBtn(){
        if(np_newPassword.getText().isEmpty() || np_confirmPassword.getText().isEmpty()){
            alert =new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
        }else {
            try{
                if(np_newPassword.getText().equals(np_confirmPassword.getText())){
                    String updatePass="UPDATE client SET password = '"+np_newPassword.getText()+"', question = '"
                            +fp_question.getSelectionModel().getSelectedItem()+"', answer = '"
                            +fp_answer.getText()+"' WHERE username = '"+fp_username.getText()+"'";

                    prepare=connect.prepareStatement(updatePass);
                    prepare.executeUpdate();

                    alert =new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully changed Password!");
                    alert.showAndWait();

                    si_loginForm.setVisible(true);
                    np_newPassForm.setVisible(false);

                    np_newPassword.setText("");
                    np_confirmPassword.setText("");
                    fp_question.getSelectionModel().clearSelection();
                    fp_answer.setText("");
                    fp_username.setText("");
                }else {
                    alert =new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Not match");
                    alert.showAndWait();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void forgtoPassQuestionList(){
        List<String> listQ=new ArrayList<>();
        for(String data:questionList){
            listQ.add(data);
        }

        ObservableList listData=FXCollections.observableArrayList(listQ);
        fp_question.setItems(listData);
    }

    public void backToLoginForm(){
        si_loginForm.setVisible(true);
        question_form.setVisible(false);
    }

    public void backToQuestionForm(){
        question_form.setVisible(true);
        np_newPassForm.setVisible(false);
    }

    public void switchForm(ActionEvent event){

        TranslateTransition slider = new TranslateTransition();

        if(event.getSource() == CreateAccountBtn){
            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished((ActionEvent e) ->{
                alreadyHaveAccount.setVisible(true);
                CreateAccountBtn.setVisible(false);

                question_form.setVisible(false);
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);

                regLquestionList();
            });

            slider.play();
        } else if (event.getSource() == alreadyHaveAccount) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished((ActionEvent e) ->{
                alreadyHaveAccount.setVisible(false);
                CreateAccountBtn.setVisible(true);

                question_form.setVisible(false);
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);
            });

            slider.play();
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
