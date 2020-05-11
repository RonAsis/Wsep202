import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Store} from '../../../../shared/store.model';
import {ShareService} from '../../../../services/share.service';
import {Product} from '../../../../shared/product.model';
import {StoreService} from '../../../../services/store.service';

@Component({
  selector: 'app-edit-product',
  templateUrl: './edit-product.component.html',
  styleUrls: ['./edit-product.component.css']
})
export class EditProductComponent implements OnInit {
  constructor(private shareService: ShareService,
              private storeService: StoreService) { }

  store: Store;
  productItems: Product[];

  @Output() productItemDeleted = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();
  @Output() productItemChanged = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();

  ngOnInit(): void {
    this.store = this.shareService.storeSelected;
    this.productItems = this.store.products;
  }

  onProductItemDeleted(productData: { productSn: number, storeId: number }) {
    this.storeService.deleteProductFromStore(productData.productSn, productData.storeId)
      .subscribe(response => {
        if (response){
          this.productItems = this.productItems.filter(product => product.productSn !== productData.productSn);
        }
      });
  }

  onProductItemChanged(productData: { productSn: number, storeId: number }) {
    this.productItemChanged.emit({
      productSn: productData.productSn,
      storeId: productData.storeId
    });
  }

  onProductItemAdded(product: Product) {
    this.productItems.push(product);
  }
}
