import {Component, Input, OnInit} from '@angular/core';
import {Product} from '../../../../shared/product.model';
import {ProductService} from '../../../../services/product.service';

@Component({
  selector: 'app-product-item',
  templateUrl: './product-item.component.html',
  styleUrls: ['./product-item.component.css']
})
export class ProductItemComponent implements OnInit {
  @Input() product: Product;

  constructor(private productService: ProductService){ }

  ngOnInit(): void {
  }
  onSelected() {
    this.productService.productSelectedEvent.emit(this.product);
  }
}
