export class BillingAddress {
  constructor(public  customerFullName: string,
              public address: string,
              public city: string,
              public country: string,
              public zipCode: string) {}
}
