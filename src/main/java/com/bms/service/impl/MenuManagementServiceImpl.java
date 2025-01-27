package com.bms.service.impl;

import com.bms.entity.MenuMst;
import com.bms.entity.UserMst;
import com.bms.repository.MenuMstRepository;
import com.bms.service.MenuManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bms.util.CommonConstant.MENU_TYPE_MAIN_MENU;
import static com.bms.util.CommonConstant.MENU_TYPE_SUB_MENU;
import static com.bms.util.ExceptionMessages.ROLE_ID_CANNOT_BE_EMPTY;

@Service
public class MenuManagementServiceImpl implements MenuManagementService {

    private MenuMstRepository menuMstRepository;

    /**
     * This method is used to get logged-in user's menu list
     *
     * @return List of menus according to logged-in user's role
     */
    @Override
    public ResponseEntity<Object> getUserMenuList() {

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Integer> roleIdList = user.getRoleIdList();

        if (roleIdList == null || roleIdList.isEmpty()) {
            throw new RuntimeException(ROLE_ID_CANNOT_BE_EMPTY);
        }

        Set<MenuMst> menuListByRoleId = menuMstRepository.getMenuListByRoleId(roleIdList);

        // Preload menus into a Map for quicker lookup
        Map<Integer, MenuMst> menuById = menuListByRoleId.stream()
                .collect(Collectors.toMap(MenuMst::getId, menu -> menu));

        // Use a Map to store main menus for faster access
        Map<Integer, MenuMst> mainMenuMap = new HashMap<>();
        List<MenuMst> menuList = new ArrayList<>();

        menuListByRoleId.forEach(menu -> {
            if (menu.getMenuType().equals(MENU_TYPE_MAIN_MENU)) {
                addMainMenu(menu, mainMenuMap, menuList);
            } else if (menu.getMenuType().equals(MENU_TYPE_SUB_MENU)) {
                addSubMenu(menu, mainMenuMap, menuById, menuList, menuMstRepository);
            }
        });


        return new ResponseEntity<>(menuList, HttpStatus.OK);
    }

    /**
     * This method is used to add main menu
     */
    private void addMainMenu(MenuMst menu, Map<Integer, MenuMst> mainMenuMap, List<MenuMst> menuList) {
        if (!mainMenuMap.containsKey(menu.getId())) {
            menuList.add(menu);
            mainMenuMap.put(menu.getId(), menu);
        }
    }

    /**
     * This method is used to add sub menu
     */
    private void addSubMenu(MenuMst menu, Map<Integer, MenuMst> mainMenuMap, Map<Integer, MenuMst> menuById,
                            List<MenuMst> menuList, MenuMstRepository menuMstRepository) {
        Integer mainMenuId = menu.getParentId();
        MenuMst mainMenu = mainMenuMap.get(mainMenuId);

        if (mainMenu != null) {
            mainMenu.getSubMenuList().add(menu);
        } else {
            mainMenu = menuById.getOrDefault(mainMenuId, menuMstRepository.findById(mainMenuId).orElse(null));
            if (mainMenu != null) {
                mainMenu.getSubMenuList().add(menu);
                menuList.add(mainMenu);
                mainMenuMap.put(mainMenu.getId(), mainMenu);
            }
        }
    }


    @Autowired
    public void setMenuMstRepository(MenuMstRepository menuMstRepository) {
        this.menuMstRepository = menuMstRepository;
    }
}
