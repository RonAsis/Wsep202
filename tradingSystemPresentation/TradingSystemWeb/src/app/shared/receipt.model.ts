import {Product} from './product.model';

export class Receipt {
  constructor(public receiptSn: number,
              public storeId: number,
              public username: string,
              public purchaseDate: Date,
              public amountToPay: number,
              public productsBought: Map<Product, number>) {}
}
