package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SellerOwnerController {

    private final SellerOwnerService sellerOwnerService;

    /**
     * View store purchase history
     */
    @MessageMapping("/view-purchase-history-of-owner")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-purchase-history-of-owner")
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(String ownerUsername, int storeId){
        return sellerOwnerService.viewPurchaseHistoryOfOwner(ownerUsername,storeId);
    }

    /**
     * add product
     */
    @MessageMapping("/add-product")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/add-product")
    public boolean addProduct(String ownerUsername, int storeId, String productName, String category, int amount, double cost ){
        return sellerOwnerService.addProduct(ownerUsername, storeId, productName, category, amount, cost);
    }

    /**
     * remove product
     */
    @MessageMapping("/delete-product-from-store")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/delete-product-from-store")
    public boolean deleteProductFromStore(String ownerUsername, int storeId, int productSn){
        return sellerOwnerService.deleteProductFromStore(ownerUsername, storeId, productSn);
    }

    /**
     * edit product
     */
    @MessageMapping("/edit-product")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/edit-product")
    public boolean editProduct(String ownerUsername, int storeId,int productSn, String productName, String category, int amount, double cost ){
        return sellerOwnerService.editProduct(ownerUsername, storeId, productSn, productName, category, amount, cost);
    }

    /**
     * add owner
     */
    @MessageMapping("/add-owner")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/add-owner")
    public boolean addOwner(String ownerUsername, int storeId, String newOwnerUsername){
        return sellerOwnerService.addOwner(ownerUsername, storeId, newOwnerUsername);
    }

    /**
     * remove manager
     */
    @MessageMapping("/remove-manager")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/remove-manager")
    public boolean removeManager(String ownerUsername, int storeId, String newManagerUsername){
        return sellerOwnerService.removeManager(ownerUsername, storeId, newManagerUsername);
    }

    /**
     * add permission
     */
    @MessageMapping("/add-permission")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/add-permission")
    public boolean addPermission(String ownerUsername, int storeId, String newManagerUsername, String permission){
        return sellerOwnerService.addPermission(ownerUsername, storeId, newManagerUsername, permission);
    }

    /**
     * add manager
     */
    @MessageMapping("/add-manager")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/add-manager")
    public boolean addManager(String ownerUsername, int storeId, String newManagerUsername){
        return sellerOwnerService.addManager(ownerUsername, storeId, newManagerUsername);
    }
}
