package com.xtremis.daedo.tkstrike.ui.controller.externalscreen;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.xtremis.daedo.tkstrike.om.ExternalScreenViewId;
import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;


public abstract class CommonExternalScreenMainController<NE extends INetworkConfigurationEntry, ESB extends ExternalScoreboardController, EHTA extends TkStrikeController, EHTJ extends TkStrikeController>
		extends CommonTkStrikeBaseController<NE> {

	@FXML
	private Pane pnWrapper;

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		getExternalScoreboardController().setEnabled(true);
		_updateView(getAppStatusWorker().getExternalScreenView());
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {
				CommonExternalScreenMainController.this.getExternalScoreboardController().getOnWindowCloseEventHandler().handle(windowEvent);
				CommonExternalScreenMainController.this.getExternalHTAthletesController().getOnWindowCloseEventHandler().handle(windowEvent);
				CommonExternalScreenMainController.this.getExternalHTJudgesController().getOnWindowCloseEventHandler().handle(windowEvent);
			}
		};
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {}

	@Override
	public void afterPropertiesSet() throws Exception {
		getAppStatusWorker().externalScreenViewProperty().addListener(new ChangeListener<ExternalScreenViewId>() {

			@Override
			public void changed(ObservableValue<? extends ExternalScreenViewId> observableValue, ExternalScreenViewId externalScreenViewId,
					ExternalScreenViewId newScreenView) {
				CommonExternalScreenMainController.this._updateView(newScreenView);
			}
		});
	}

	private void _updateView(ExternalScreenViewId newExternalViewId) {
		if(ExternalScreenViewId.SCOREBOARD.equals(newExternalViewId)) {
			this.pnWrapper.getChildren().clear();
			this.pnWrapper.getChildren().add(getExternalScoreboardController().getRootView());
			getExternalScoreboardController().onWindowShowEvent();
		} else if(ExternalScreenViewId.HT_ATHLETES.equals(newExternalViewId)) {
			this.pnWrapper.getChildren().clear();
			this.pnWrapper.getChildren().add(getExternalHTAthletesController().getRootView());
			getExternalHTAthletesController().onWindowShowEvent();
		} else if(ExternalScreenViewId.HT_JUDGES.equals(newExternalViewId)) {
			this.pnWrapper.getChildren().clear();
			this.pnWrapper.getChildren().add(getExternalHTJudgesController().getRootView());
			getExternalHTJudgesController().onWindowShowEvent();
		}
	}

	abstract ESB getExternalScoreboardController();

	abstract EHTA getExternalHTAthletesController();

	abstract EHTJ getExternalHTJudgesController();
}
