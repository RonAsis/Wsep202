import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { GuestComponent } from './guest/guest.component';
import { BuyerComponent } from './logged-in-user/buyer/buyer.component';
import { SellerComponent } from './logged-in-user/seller/seller.component';
import { RegisterComponent } from './guest/register/register.component';
import { LoginComponent } from './guest/login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { HeaderGuestComponent } from './guest/header-guest/header-guest.component';
import { StoresComponent } from './guest/stores/stores.component';
import { ProductsComponent } from './guest/products/products.component';
import { ShoppingCartComponent } from './guest/shopping-cart/shopping-cart.component';
import { StoreItemComponent } from './guest/stores/store-list/store-item/store-item.component';
import { StoreListComponent } from './guest/stores/store-list/store-list.component';
import { StoreDetailComponent } from './guest/stores/store-detail/store-detail.component';
import {ProductListComponent} from './guest/products/product-list/product-list.component';
import {ProductDetailComponent} from './guest/products/product-detail/product-detail.component';
import { ProductItemComponent } from './guest/products/product-list/product-item/product-item.component';
import { NgxBootstrapSliderModule } from 'ngx-bootstrap-slider';
import { Ng5SliderModule } from 'ng5-slider';
import { Ng2SearchPipeModule } from 'ng2-search-filter';
import {FormsModule} from '@angular/forms';
import { ProductRankPipe } from './pipes/product-rank.pipe';
import { RangePriceProductPipe } from './pipes/range-price-product.pipe';
import { StoreRankPipe } from './pipes/store-rank.pipe';
import { ProductCategoryPipe } from './pipes/product-category.pipe';
import { LogoutComponent } from './logged-in-user/logout/logout.component';
import { HistoryPurchaseComponent } from './logged-in-user/history-purchase/history-purchase.component';
import { OwnedStoresComponent } from './logged-in-user/owned-stores/owned-stores.component';
import { ManagedStoresComponent } from './logged-in-user/managed-stores/managed-stores.component';
import { OpenStoreComponent } from './logged-in-user/open-store/open-store.component';
import { ReceiptElementComponent } from './logged-in-user/history-purchase/recipet-element/receipt-element.component';
import { ProductInReceiptComponent } from './logged-in-user/history-purchase/recipet-element/product-in-receipt/product-in-receipt.component';
import { MapToArrayPipe } from './pipes/map-to-array.pipe';
import {LoggedInUserComponent} from './logged-in-user/logged-in-user.component';
import {HeaderLoggedInUserComponent} from './logged-in-user/header-logging-user/header-logging-user.component';
import { ReversePipe } from './pipes/reverse-pipe.pipe';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToasterModule} from 'angular2-toaster';
import { UsersComponent } from './logged-in-user/admin/users/users.component';
import {AdminComponent} from './logged-in-user/admin/admin.component';
import { UserElementComponent } from './logged-in-user/admin/users/user-list/user-element/user-element.component';
import { UserListComponent } from './logged-in-user/admin/users/user-list/user-list.component';
import { UserDetailComponent } from './logged-in-user/admin/users/user-detail/user-detail.component';
import {ShoppingCartModule} from 'ng-shopping-cart';
import {Product} from './shared/product.model';
import { CartItemComponent } from './guest/shopping-cart/cart-item/cart-item.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    GuestComponent,
    SellerComponent,
    BuyerComponent,
    RegisterComponent,
    LoginComponent,
    HeaderGuestComponent,
    StoresComponent,
    ProductsComponent,
    ShoppingCartComponent,
    StoreItemComponent,
    StoreListComponent,
    StoreDetailComponent,
    ProductListComponent,
    ProductDetailComponent,
    ProductItemComponent,
    ProductRankPipe,
    RangePriceProductPipe,
    StoreRankPipe,
    ProductCategoryPipe,
    LoggedInUserComponent,
    LogoutComponent,
    HistoryPurchaseComponent,
    OwnedStoresComponent,
    ManagedStoresComponent,
    OpenStoreComponent,
    ReceiptElementComponent,
    ProductInReceiptComponent,
    MapToArrayPipe,
    HeaderLoggedInUserComponent,
    ReversePipe,
    UsersComponent,
    AdminComponent,
    UserElementComponent,
    UserListComponent,
    UserDetailComponent,
    CartItemComponent
  ],
    imports: [
        Ng2SearchPipeModule,
        Ng5SliderModule,
        BrowserModule,
        BrowserAnimationsModule,
        ToasterModule.forRoot(),
        AppRoutingModule,
        HttpClientModule,
        NgxBootstrapSliderModule,
        FormsModule,
      ShoppingCartModule.forRoot({ // <-- Add the cart module to your root module
        itemType: Product, // <-- Configuration is optional
        serviceType: 'localStorage',
        serviceOptions: {
          storageKey: 'NgShoppingCart',
          clearOnError: true
        }
        })
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
