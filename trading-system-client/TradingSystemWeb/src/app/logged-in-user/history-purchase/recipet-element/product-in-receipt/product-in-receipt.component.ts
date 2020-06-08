import {Component, Input, OnInit} from '@angular/core';
import {Product} from '../../../../shared/product.model';

@Component({
  selector: 'app-product-in-receipt',
  templateUrl: './product-in-receipt.component.html',
  styleUrls: ['./product-in-receipt.component.css']
})
export class ProductInReceiptComponent implements OnInit {
  @Input() product: Product;

  constructor() { }

  ngOnInit(): void {
  }

}
