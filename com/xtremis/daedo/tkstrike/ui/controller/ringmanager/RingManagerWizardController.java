package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import com.xtremis.daedo.tkstrike.service.MatchConfigurationService;
import com.xtremis.daedo.tkstrike.service.RulesService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;


public class RingManagerWizardController extends TkStrikeBaseController {

	@FXML
	private AnchorPane pnMain;

	@FXML
	private Button btFinish;

	@FXML
	private Button btNext;

	@FXML
	private Button btPrevious;

	@FXML
	private Button btCancel;

	@FXML
	private Label lblMatchNumber;

	@FXML
	private Label lblPhase;

	@FXML
	private Label lblSubCategory;

	@FXML
	private Label lblGender;

	@FXML
	private Label lblCategory;

	private HashMap<Integer, StepWizardController> stepsNodes = new HashMap<>();

	private SimpleIntegerProperty currentStep = new SimpleIntegerProperty(0);

	@Autowired
	private MatchNumberController matchNumberController;

	@Autowired
	private PhaseController phaseController;

	@Autowired
	private SubCategoryController subCategoryController;

	@Autowired
	private GenderController genderController;

	@Autowired
	private CategoryController categoryController;

	@Autowired
	private AthletesInformationController athletesInformationController;

	@Autowired
	private MatchConfigurationController matchConfigurationController;

	@Autowired
	private MatchConfigurationService matchConfigurationService;

	@Autowired
	private RulesService rulesService;

	private MatchConfigurationEntry matchConfigurationEntry = new MatchConfigurationEntry();

	private boolean wizardFinished = false;

	private boolean wizardCanceled = false;

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				RingManagerWizardController.this.clearAllWizard();
				RingManagerWizardController.this.wizardFinished = false;
				RingManagerWizardController.this.wizardCanceled = true;
				RingManagerWizardController.this.matchConfigurationEntry.sensorsGroupProperty().set(SensorsGroup.GROUP1.toString());
				RulesEntry rulesEntry = null;
				try {
					rulesEntry = (RulesEntry)RingManagerWizardController.this.rulesService.getRulesEntry();
				} catch(TkStrikeServiceException e) {
					RingManagerWizardController.this.manageException(e, "getRulesEntry", null);
				}
				if(rulesEntry != null) {
					RingManagerWizardController.this.matchConfigurationEntry.roundsConfigProperty().set(rulesEntry.getRoundsConfig());
					RingManagerWizardController.this.matchConfigurationEntry.differencialScoreProperty().set(rulesEntry.getDifferencialScore());
				}
				RingManagerWizardController.this.stepsNodes.put(Integer.valueOf(0), RingManagerWizardController.this.matchNumberController);
				RingManagerWizardController.this.stepsNodes.put(Integer.valueOf(1), RingManagerWizardController.this.phaseController);
				RingManagerWizardController.this.stepsNodes.put(Integer.valueOf(2), RingManagerWizardController.this.subCategoryController);
				RingManagerWizardController.this.stepsNodes.put(Integer.valueOf(3), RingManagerWizardController.this.genderController);
				RingManagerWizardController.this.stepsNodes.put(Integer.valueOf(4), RingManagerWizardController.this.categoryController);
				RingManagerWizardController.this.stepsNodes.put(Integer.valueOf(5), RingManagerWizardController.this.athletesInformationController);
				RingManagerWizardController.this.stepsNodes.put(Integer.valueOf(6), RingManagerWizardController.this.matchConfigurationController);
				RingManagerWizardController.this.refreshView(Integer.valueOf( - 1), Integer.valueOf(0));
			}
		});
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {}
		};
	}

	protected void clearAllWizard() {
		for(StepWizardController stepWizardController : this.stepsNodes.values())
			stepWizardController.clearForm();
		this.stepsNodes.clear();
		this.currentStep.set(0);
		this.matchConfigurationEntry = new MatchConfigurationEntry();
	}

	public void doNext() {
		StepWizardController currController = this.stepsNodes.get(Integer.valueOf(this.currentStep.intValue()));
		if(currController.isFormValid() &&
				this.currentStep.get() < this.stepsNodes.keySet().size())
			this.currentStep.set(this.currentStep.get() + 1);
	}

	public void doPrevious() {
		if(this.currentStep.get() > 0)
			this.currentStep.set(this.currentStep.get() - 1);
	}

	public void doFinish() {
		StepWizardController currController = this.stepsNodes.get(Integer.valueOf(this.currentStep.intValue()));
		if(currController.isFormValid()) {
			MatchConfigurationEntry matchConfigurationEntry = currController.getMatchConfigurationEntry();
			this.matchConfigurationEntry = matchConfigurationEntry;
			try {
				this.matchConfigurationEntry = this.matchConfigurationService.createByEntry(this.matchConfigurationEntry);
				this.wizardFinished = true;
				this.wizardCanceled = false;
				doCloseThisStage();
			} catch(TkStrikeServiceException e) {
				manageException(e, "Create MatchConfiguration", null);
			}
		}
	}

	public void doCancel() {
		this.wizardFinished = false;
		this.wizardCanceled = true;
		doCloseThisStage();
	}

	protected void refreshView(Number prevStep, Number newStep) {
		Integer currentStepValue = this.currentStep.getValue();
		StepWizardController prevStepController = this.stepsNodes.get(Integer.valueOf(prevStep.intValue()));
		StepWizardController tkStrikeController = this.stepsNodes.get(currentStepValue);
		if(tkStrikeController != null) {
			if(prevStepController != null)
				this.matchConfigurationEntry = prevStepController.getMatchConfigurationEntry();
			Node theNode = tkStrikeController.getRootView();
			if(theNode != null) {
				this.pnMain.getChildren().clear();
				this.pnMain.getChildren().add(theNode);
				AnchorPane.setTopAnchor(theNode, Double.valueOf(0.0D));
				AnchorPane.setLeftAnchor(theNode, Double.valueOf(0.0D));
				AnchorPane.setRightAnchor(theNode, Double.valueOf(0.0D));
				AnchorPane.setBottomAnchor(theNode, Double.valueOf(0.0D));
				tkStrikeController.onWindowShowEvent();
				tkStrikeController.setMatchConfigurationEntry(this.matchConfigurationEntry);
				if(currentStepValue.intValue() == 0) {
					this.btPrevious.setDisable(true);
					this.btNext.setDisable(false);
					this.btFinish.setDisable(true);
				} else if(currentStepValue.intValue() + 1 == this.stepsNodes.keySet().size()) {
					this.btNext.setDisable(true);
					this.btPrevious.setDisable(false);
					this.btFinish.setDisable(false);
				} else {
					this.btPrevious.setDisable(false);
					this.btNext.setDisable(false);
					this.btFinish.setDisable(true);
				}
				_doBreadcrumb();
			}
		}
	}

	private void _doBreadcrumb() {
		if(this.matchConfigurationEntry != null) {
			if(this.currentStep.get() > 0 && this.matchConfigurationEntry.getMatchNumber() != null) {
				this.lblMatchNumber.setText(this.matchConfigurationEntry.getMatchNumber() + " >");
			} else {
				this.lblMatchNumber.setText("");
			}
			if(this.currentStep.get() > 1 && this.matchConfigurationEntry.getPhase() != null && this.matchConfigurationEntry.getPhase()
					.getId() != null) {
				this.lblPhase.setText(this.matchConfigurationEntry.getPhase().getName() + " >");
			} else {
				this.lblPhase.setText("");
			}
			if(this.currentStep.get() > 2 && this.matchConfigurationEntry.getSubCategory() != null && this.matchConfigurationEntry.getSubCategory()
					.getId() != null) {
				this.lblSubCategory.setText(this.matchConfigurationEntry.getSubCategory().getName() + " >");
			} else {
				this.lblSubCategory.setText("");
			}
			if(this.matchConfigurationEntry.getGender() != null && this.currentStep.getValue().intValue() > 3) {
				this.lblGender.setText(getMessage("enum.gender." + this.matchConfigurationEntry.getGender().toString()) + " >");
			} else {
				this.lblGender.setText("");
			}
			if(this.currentStep.get() > 4 && this.matchConfigurationEntry.getCategory() != null && this.matchConfigurationEntry.getCategory()
					.getId() != null) {
				this.lblCategory.setText(this.matchConfigurationEntry.getCategory().getName() + " >");
			} else {
				this.lblCategory.setText("");
			}
		}
	}

	public MatchConfigurationEntry getMatchConfigurationEntry() {
		return this.matchConfigurationEntry;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.currentStep.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number prevStep, Number newStep) {
				RingManagerWizardController.this.refreshView(prevStep, newStep);
			}
		});
		this.lblMatchNumber.setCursor(Cursor.HAND);
		this.lblMatchNumber.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				RingManagerWizardController.this.currentStep.set(0);
			}
		});
		this.lblPhase.setCursor(Cursor.HAND);
		this.lblPhase.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				RingManagerWizardController.this.currentStep.set(1);
			}
		});
		this.lblSubCategory.setCursor(Cursor.HAND);
		this.lblSubCategory.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				RingManagerWizardController.this.currentStep.set(2);
			}
		});
		this.lblGender.setCursor(Cursor.HAND);
		this.lblGender.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				RingManagerWizardController.this.currentStep.set(3);
			}
		});
		this.lblCategory.setCursor(Cursor.HAND);
		this.lblCategory.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				RingManagerWizardController.this.currentStep.set(4);
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.matchNumberController.submitFormProperty().addListener(new StepSubmitFormListener());
		this.phaseController.submitFormProperty().addListener(new StepSubmitFormListener());
		this.subCategoryController.submitFormProperty().addListener(new StepSubmitFormListener());
		this.genderController.submitFormProperty().addListener(new StepSubmitFormListener());
		this.categoryController.submitFormProperty().addListener(new StepSubmitFormListener());
		this.athletesInformationController.submitFormProperty().addListener(new StepSubmitFormListener());
		this.matchConfigurationController.submitFormProperty().addListener(new StepSubmitFormListener());
	}

	public boolean isWizardCanceled() {
		return this.wizardCanceled;
	}

	private class StepSubmitFormListener implements ChangeListener<Boolean> {

		private StepSubmitFormListener() {}

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean forceSubmit) {
			if(forceSubmit.booleanValue())
				RingManagerWizardController.this.doNext();
		}
	}
}
