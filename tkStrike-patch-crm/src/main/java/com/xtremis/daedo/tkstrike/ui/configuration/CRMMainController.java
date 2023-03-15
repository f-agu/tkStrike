package com.xtremis.daedo.tkstrike.ui.configuration;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.WindowEvent;


public class CRMMainController extends BaseConfigurationMainController<NetworkConfigurationEntry> {

	@FXML
	private TabPane tabPane;

	@Autowired
	private CRMDisciplineController crmDisciplineController;

	@Autowired
	private CRMPointController crmPointController;

	@Autowired
	private CRMTestNetworkController crmTestNetworkController;

	public void doCloseConfig() {
		doCloseThisStage();
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		this.tabPane.getSelectionModel().selectFirst();
		this.tabPane.getSelectionModel().getSelectedItem().setContent(crmDisciplineController.getRootView());
		crmDisciplineController.onWindowShowEvent();
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {
				// crmPointController.getOnWindowCloseEventHandler().handle(windowEvent);
			}
		};
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
				if("tabDiscipline".equals(t1.getId())) {
					t1.setContent(crmDisciplineController.getRootView());
					crmDisciplineController.onWindowShowEvent();
				} else if("tabPoint".equals(t1.getId())) {
					t1.setContent(crmPointController.getRootView());
					crmPointController.onWindowShowEvent();
				} else if("tabTestNetwork".equals(t1.getId())) {
					t1.setContent(crmTestNetworkController.getRootView());
					crmTestNetworkController.onWindowShowEvent();
				}
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
