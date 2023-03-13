package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import java.util.List;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;

public abstract class TkStrikeBaseTableManagementController<E extends Entity, D extends Entry, S extends TkStrikeService<E, D>> extends CommonTkStrikeBaseController {
  protected abstract TkStrikeService<E, D> getTkStrikeService();
  
  protected abstract ObservableList<D> getObservableList4Table();
  
  protected abstract TableView<D> getTableView();
  
  protected abstract ProgressIndicator getProgressIndicator();
  
  protected void showTableProgressIndicator(final boolean show) {
    Platform.runLater(new Runnable() {
          public void run() {
            TkStrikeBaseTableManagementController.this.getTableView().setVisible(!show);
            TkStrikeBaseTableManagementController.this.getProgressIndicator().setVisible(show);
          }
        });
  }
  
  protected void refreshTable() {
    showTableProgressIndicator(true);
    TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
          public Void call() throws Exception {
            final ObservableList<D> theList = TkStrikeBaseTableManagementController.this.getObservableList4Table();
            try {
              final List<D> allEntries = TkStrikeBaseTableManagementController.this.getTkStrikeService().findAllEntries();
              Platform.runLater(new Runnable() {
                    public void run() {
                      theList.clear();
                      if (allEntries != null && allEntries.size() > 0)
                        theList.addAll(allEntries); 
                      TkStrikeBaseTableManagementController.this.showTableProgressIndicator(false);
                    }
                  });
            } catch (TkStrikeServiceException e) {
              TkStrikeBaseTableManagementController.this.manageException((Throwable)e, "Updating a table", null);
            } 
            return null;
          }
        });
  }
}
