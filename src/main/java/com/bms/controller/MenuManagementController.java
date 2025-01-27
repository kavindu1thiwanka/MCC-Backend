package com.bms.controller;

import com.bms.service.MenuManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import static com.bms.controller.abst.Mappings.GET_MENU_LIST_V1;
import static com.bms.controller.abst.Mappings.MENU;

@RestController
@RequestMapping(MENU)
@RequestScope
public class MenuManagementController {

    private MenuManagementService menuManagementService;

    @GetMapping(GET_MENU_LIST_V1)
    public ResponseEntity<Object> getUserMenuList() {
        return menuManagementService.getUserMenuList();
    }

    @Autowired
    public void setMenuManagementService(MenuManagementService menuManagementService) {
        this.menuManagementService = menuManagementService;
    }
}
