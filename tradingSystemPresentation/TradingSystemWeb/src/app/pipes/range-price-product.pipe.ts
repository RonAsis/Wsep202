import { Pipe, PipeTransform } from '@angular/core';
import {Product} from '../shared/product.model';
import {min} from 'rxjs/operators';

@Pipe({
  name: 'rangePriceProduct'
})
export class RangePriceProductPipe implements PipeTransform {

  transform(products: Product[], minPrice: number, maxPrice: number): Product[] {
    if (!products) {
      return products;
    }
    // filter items array, items which match and return true will be
    // kept, false will be filtered out
    return products.filter(product => minPrice <= product.cost && product.cost <= maxPrice);
  }

}
