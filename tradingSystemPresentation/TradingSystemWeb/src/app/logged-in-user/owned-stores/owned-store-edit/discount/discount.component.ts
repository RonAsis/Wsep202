import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import {FormGroup} from '@angular/forms';
import {FormlyFieldConfig, FormlyFormOptions} from '@ngx-formly/core';
import {FormlyJsonschema} from '@ngx-formly/core/json-schema';
import {Store} from '../../../../shared/store.model';
import {Manager} from '../../../../shared/manager.model';
import {StoreService} from '../../../../services/store.service';
import {Discount} from '../../../../shared/discount.model';

@Component({
  selector: 'app-discount',
  templateUrl: './discount.component.html',
  styleUrls: ['./discount.component.css']
})
export class DiscountComponent implements OnInit {

  @Input() store: Store;
  discounts: Discount[];
  constructor(private storeService: StoreService) {
    this.discounts = [];
  }

  ngOnInit(): void {
    this.storeService.getStoreDiscounts(this.store.storeId).subscribe(
      response => {
        if (response !== undefined && response !== null){
          this.discounts = response;
        }
      }
    );
  }

  onDiscountAdded(discount: Discount) {
    // this.discounts.push(discount);
  }
  onDiscountItemDeleted(discount: Discount) {
    this.storeService.removeDiscount(discount.discountId, this.store.storeId)
      .subscribe(response => {
        if (response){
          this.ngOnInit();
        }
      });
  }

  onDiscountItemChanged(manager: Manager) {
    this.ngOnInit();
  }
}
