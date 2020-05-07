import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../../services/user.service';
import {PaymentDetails} from '../../../shared/paymentDetails.model';
import {BillingAddress} from '../../../shared/billingAddress.model';

@Component({
  selector: 'app-purchase-shopping-cart',
  templateUrl: './purchase-shopping-cart.component.html',
  styleUrls: ['./purchase-shopping-cart.component.css']
})
export class PurchaseShoppingCartComponent implements OnInit {

  @ViewChild('fullNameInput', {static: false}) fullNameInputRef: ElementRef;
  @ViewChild('addressInput', {static: false}) addressInputRef: ElementRef;
  @ViewChild('cityInput', {static: false}) cityInputRef: ElementRef;
  @ViewChild('countryInput', {static: false}) countryInputRef: ElementRef;
  @ViewChild('zipCodeInput', {static: false}) zipCodeInputRef: ElementRef;
  @ViewChild('creditCardNumberInput', {static: false}) creditCardNumberInputRef: ElementRef;
  @ViewChild('ccvInput', {static: false}) ccvInputRef: ElementRef;
  @ViewChild('idInput', {static: false}) idInputRef: ElementRef;

  @Input() cartTotal: number;
  @Input() cartTotalAfterDiscount: number;

  messageColor: any;
  message: any;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getTotalPrice();
  }

  private getTotalPrice() {
    this.userService.getTotalPriceOfShoppingCart().subscribe(prices => {
      this.cartTotal = prices.key;
      this.cartTotalAfterDiscount = prices.value;
    });
  }

  OnPurchase() {
    const paymentDetails = new PaymentDetails(this.creditCardNumberInputRef.nativeElement.value,
      this.ccvInputRef.nativeElement.value,
      this.idInputRef.nativeElement.value);
    const billingAddress = new BillingAddress(this.fullNameInputRef.nativeElement.value,
      this.addressInputRef.nativeElement.value,
      this.cityInputRef.nativeElement.value,
      this.countryInputRef.nativeElement.value,
      this.zipCodeInputRef.nativeElement.value);
    console.log(paymentDetails);
    console.log(billingAddress);
    this.userService.purchaseShoppingCart(paymentDetails, billingAddress)
      .subscribe(receipts => {
        if (receipts !== undefined){
          console.log(receipts);
        }
      }, error => this.errorMessage(error.value));
  }

  errorMessage(message: string){
    this.message = message;
    this.messageColor = 'red';
  }

  numberOnly(event): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;

  }
}
