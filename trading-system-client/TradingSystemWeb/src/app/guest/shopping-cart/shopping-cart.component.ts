import {Component, OnInit, EventEmitter, Output, Input} from '@angular/core';
import {Product} from '../../shared/product.model';
import {UserService} from '../../services/user.service';
import {ShoppingCart} from '../../shared/shoppingCart.model';
import {Observable} from 'rxjs';
import {ShareService} from '../../services/share.service';
import {ProductShoppingCartDto} from '../../shared/productShoppingCartDto.model';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css']
})
export class ShoppingCartComponent implements OnInit {

  messageColor: any;
  message: any;

  @Input() cartTotal: number;
  @Input() cartTotalAfterDiscount: number;
  @Input() cartItems: ProductShoppingCartDto[];

  @Output() cartItemDeleted = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();
  @Output() cartItemChanged = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();
  constructor(private userService: UserService, private shareService: ShareService) {
    this.cartItems = [];
  }

  ngOnInit(): void {
    this.getShoppingCartItems();
    this.getTotalPrice();
    }

  private getShoppingCartItems() {
      this.userService.getShoppingCart()
        .subscribe(response => {
          if (response !== undefined && response !== null){
            this.cartItems = response;
          }
        });
  }

  onCartItemDeleted(productData: { productSn: number, storeId: number }) {
    this.userService.removeCartItem(productData.productSn, productData.storeId).subscribe( response => {
      if (response){
        this.getShoppingCartItems();
        this.getTotalPrice();
      }
    });
  }

  private getTotalPrice() {
    this.userService.getTotalPriceOfShoppingCart().subscribe(prices => {
      this.cartTotal = prices.key;
      this.cartTotalAfterDiscount = prices.value;
    });
  }

  onCartItemChanged(productData: { productSn: number, storeId: number }) {
    this.cartItemChanged.emit({
      productSn: productData.productSn,
      storeId: productData.storeId
    });
    this.getTotalPrice();
  }

  onPurchaseShoppingCart() {
    if (this.thereIsProducts() ){
      this.shareService.featureSelected.emit('Purchase-shopping-cart');
    }else{
      this.errorMessage('You must select lest one product');
    }
  }

  private thereIsProducts() {
    return this.cartItems.length !== 0;
  }

  errorMessage(message: string){
    this.message = message;
    this.messageColor = 'red';
  }
}
