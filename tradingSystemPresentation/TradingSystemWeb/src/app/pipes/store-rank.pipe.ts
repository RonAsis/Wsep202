import { Pipe, PipeTransform } from '@angular/core';
import {Product} from '../shared/product.model';
import {Store} from '../shared/store.model';

@Pipe({
  name: 'storeRank'
})
export class StoreRankPipe implements PipeTransform {

  transform(stores: Store[], storeRank: number): Store[] {
    if (!stores) {
      return stores;
    }
    // filter items array, items which match and return true will be
    // kept, false will be filtered out
    return stores.filter(store => store.rank >= storeRank);
  }

}
