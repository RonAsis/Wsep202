import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { GuestComponent } from './guest/guest.component';
import { BuyerComponent } from './logged-in-user/buyer/buyer.component';
import { SellerComponent } from './logged-in-user/seller/seller.component';
import { LoggedInUserComponent } from './logged-in-user/logged-in-user.component';
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
import { HeaderLoggingUserComponent } from './logged-in-user/header-logging-user/header-logging-user.component';
import { LogoutComponent } from './logged-in-user/logout/logout.component';
import { HistoryPurchaseComponent } from './logged-in-user/history-purchase/history-purchase.component';
import { OwnedStoresComponent } from './logged-in-user/owned-stores/owned-stores.component';
import { ManagedStoresComponent } from './logged-in-user/managed-stores/managed-stores.component';
import { OpenStoreComponent } from './guest/stores/open-store/open-store.component';
import { ReceiptElementComponent } from './logged-in-user/history-purchase/recipet-element/receipt-element.component';
import { ProductInReceiptComponent } from './logged-in-user/history-purchase/recipet-element/product-in-receipt/product-in-receipt.component';
import { MapToArrayPipe } from './pipes/map-to-array.pipe';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    GuestComponent,
    SellerComponent,
    LoggedInUserComponent,
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
    HeaderLoggingUserComponent,
    LogoutComponent,
    HistoryPurchaseComponent,
    OwnedStoresComponent,
    ManagedStoresComponent,
    OpenStoreComponent,
    ReceiptElementComponent,
    ProductInReceiptComponent,
    MapToArrayPipe
  ],
  imports: [
    Ng2SearchPipeModule,
    Ng5SliderModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NgxBootstrapSliderModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
