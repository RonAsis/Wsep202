import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Product} from '../../../shared/product.model';
import {UserService} from '../../../services/user.service';
import {ProductShoppingCartDto} from '../../../shared/productShoppingCartDto.model';

@Component({
  selector: 'app-cart-item',
  templateUrl: './cart-item.component.html',
  styleUrls: ['./cart-item.component.css']
})
export class CartItemComponent implements OnInit {
  @ViewChild('amountCartItem', {static: false}) amountCartItem: ElementRef;
  @Input() cartItem: ProductShoppingCartDto;
  @Output() cartItemDeleted = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();
  @Output() cartItemChanged = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();

  onCartItemDeleted(event) {

    this.cartItemDeleted.emit({
      productSn: this.cartItem.productSn,
      storeId: this.cartItem.storeId
    });
  }

  onCartItemChanged(event) {
    this.userService.changeItemCartAmount(this.cartItem.productSn, this.cartItem.storeId, this.amountCartItem.nativeElement.value);
    const sn = event.target.getAttribute('productSn');
    const storeOfId = event.target.getAttribute('storeId');

    this.cartItemChanged.emit({
      productSn: sn,
      storeId: storeOfId
    });
  }

  constructor(private userService: UserService) {
  }

  ngOnInit() {
  }

}
