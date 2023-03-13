package com.xtremis.daedo.tkstrike.ui.controller;

import org.apache.commons.lang3.StringUtils;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.model.Entry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXButton;


public abstract class TkStrikeBaseTableWithDeleteManagementController<E extends Entity, D extends Entry, S extends TkStrikeService<E, D>>
		extends TkStrikeBaseTableManagementController<E, D, S> {

	protected class DeleteEntryCell extends TableCell<D, Boolean> {

		final Button cellButton = new Button();

		public DeleteEntryCell(final TableView<D> tableView) {
			this.cellButton.getStyleClass().addAll(new String[] {"button-image-delete"});
			this.cellButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					int selectedIndex = TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell.this.getTableRow().getIndex();
					Entry entry = tableView.getItems().get(selectedIndex);
					if(entry != null && StringUtils.isNotBlank(entry.getId()))
						if(TkStrikeBaseTableWithDeleteManagementController.this.canDeleteEntryById(entry.getId())) {
							MonologFXButton mlb = new MonologFXButton();
							mlb.setDefaultButton(true);
							mlb.setIcon("/images/ic_ok.png");
							mlb.setLabel(TkStrikeBaseTableWithDeleteManagementController.this.getMessage("button.dialog.YES"));
							mlb.setType(MonologFXButton.Type.YES);
							MonologFXButton mlb2 = new MonologFXButton();
							mlb2.setCancelButton(true);
							mlb2.setIcon("/images/ic_delete.png");
							mlb2.setLabel(TkStrikeBaseTableWithDeleteManagementController.this.getMessage("button.dialog.NO"));
							mlb2.setType(MonologFXButton.Type.NO);
							MonologFX mono = new MonologFX(MonologFX.Type.QUESTION);
							mono.setModal(true);
							mono.addButton(mlb);
							mono.addButton(mlb2);
							mono.setButtonAlignment(MonologFX.ButtonAlignment.RIGHT);
							mono.setTitleText(TkStrikeBaseTableWithDeleteManagementController.this.getMessage("message.confirmDialog.title"));
							mono.setMessage(TkStrikeBaseTableWithDeleteManagementController.this.getMessage("message.confirmDialog.delete"));
							MonologFXButton.Type type = mono.showDialog();
							if(MonologFXButton.Type.YES.equals(type)) {
								try {
									TkStrikeBaseTableWithDeleteManagementController.this.getTkStrikeService().delete(entry.getId());
								} catch(TkStrikeServiceException e) {
									TkStrikeBaseTableWithDeleteManagementController.this.manageException(e, "Deleting entry", null);
								}
								TkStrikeBaseTableWithDeleteManagementController.this.refreshTable();
							}
						} else {
							TkStrikeBaseTableWithDeleteManagementController.this.showErrorDialog(TkStrikeBaseTableWithDeleteManagementController.this
									.getMessage("title.default.error"), TkStrikeBaseTableWithDeleteManagementController.this
											.getMessageEntityCantBeDeleted());
						}
				}
			});
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if( ! empty) {
				setGraphic(this.cellButton);
			} else {
				setGraphic(null);
			}
		}
	}

	protected boolean canDeleteEntryById(String entryId) {
		return getTkStrikeService().canDelete(entryId).booleanValue();
	}

	protected String getMessageEntityCantBeDeleted() {
		return getMessage("message.error.entityCantBeDeleted");
	}
}
