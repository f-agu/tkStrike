package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import javafx.fxml.Initializable;
import org.springframework.beans.factory.InitializingBean;

public interface CategoriesMainController extends Initializable, InitializingBean, TkStrikeController {
  void showCategoriesBySubCategoryId(String paramString);
}
