import {Product} from './product.model';

export class Store {
  constructor(public storeId: number,
              public storeName: string,
              public products: Product[],
              public rank: number,
              public description: string) {}
}
