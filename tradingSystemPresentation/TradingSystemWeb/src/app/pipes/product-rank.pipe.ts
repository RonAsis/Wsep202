import { Pipe, PipeTransform } from '@angular/core';
import {Product} from '../shared/product.model';

@Pipe({
  name: 'productRank'
})
export class ProductRankPipe implements PipeTransform {

  transform(products: Product[], productRank: number): Product[] {
    if (!products) {
      return products;
    }
    // filter items array, items which match and return true will be
    // kept, false will be filtered out
    return products.filter(product => product.rank >= productRank);
  }

}
