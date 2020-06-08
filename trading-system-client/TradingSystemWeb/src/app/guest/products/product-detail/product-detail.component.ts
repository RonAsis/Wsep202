import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Product} from '../../../shared/product.model';
import {UserService} from '../../../services/user.service';
import {ShareService} from '../../../services/share.service';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  @Input() product: Product;
  @ViewChild('amountProducts', {static: false}) amountProducts: ElementRef;

  constructor(private userService: UserService, private shareService: ShareService) { }

  ngOnInit(): void {
  }

  addToShoppingCart() {
    this.userService.addToShoppingCart(this.product, this.amountProducts.nativeElement.value).subscribe( (response: boolean) => {
      console.log(response);
      if (response !== undefined){
        this.shareService.featureSelected.emit('Shopping-cart');
      }
    }, error => console.log(error));
  }
}
