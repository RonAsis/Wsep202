export class PaymentDetails {
  constructor(private creditCardNumber: string,
              private ccv: string,
              private holderIDNumber: string,
              private month: string,
              private year: string,
              private holderName: string) {}
}
