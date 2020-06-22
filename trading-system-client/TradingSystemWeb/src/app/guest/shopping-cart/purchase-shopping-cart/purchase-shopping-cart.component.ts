import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../../services/user.service';
import {PaymentDetails} from '../../../shared/paymentDetails.model';
import {BillingAddress} from '../../../shared/billingAddress.model';
import {HttpErrorResponse} from '@angular/common/http';
import {ShareService} from '../../../services/share.service';


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
  @ViewChild('expirationMonth', {static: false}) expirationMonth: ElementRef;
  @ViewChild('expirationYear', {static: false}) expirationYear: ElementRef;

  @Input() cartTotal: number;
  @Input() cartTotalAfterDiscount: number;

  @ViewChild('disabledPurchase', {static: false}) disabledPurchase: boolean = false;

  messageColor: string;
  message: string;

  constructor(private userService: UserService, private shareService: ShareService) {
    this.message = '';
    this.disabledPurchase = false;
  }

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
    this.disabledPurchase = true;
    this.message = 'Waiting for details approval';
    this.messageColor = 'blue';
    if (this.checkAllDetails()) {
      const paymentDetails = new PaymentDetails(this.creditCardNumberInputRef.nativeElement.value,
        this.ccvInputRef.nativeElement.value,
        this.idInputRef.nativeElement.value,
        this.expirationMonth.nativeElement.value,
        this.expirationYear.nativeElement.value,
        this.fullNameInputRef.nativeElement.value);
      const billingAddress = new BillingAddress(this.fullNameInputRef.nativeElement.value,
        this.addressInputRef.nativeElement.value,
        this.cityInputRef.nativeElement.value,
        this.countryInputRef.nativeElement.value,
        this.zipCodeInputRef.nativeElement.value);
      this.userService.purchaseShoppingCart(paymentDetails, billingAddress)
        .subscribe(response => {
          this.disabledPurchase = false;
          if (response === undefined){
            this.errorMessage('There is no response from the server');
            this.disabledPurchase = false;
          }else{
            this.shareService.setReceipts(response);
            this.userService.deleteShoppingCart();
            this.shareService.featureSelected.emit('History-purchase');
          }
        }, (error: HttpErrorResponse) => {
          this.disabledPurchase = false;
          this.errorMessage(error.error.message);
        });
    }else{
      this.disabledPurchase = false;
    }
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

   checkAllDetails() {
    let theDetailsLegal = true;
    if (this.cityInputRef.nativeElement.value === undefined || this.cityInputRef.nativeElement.value.length === 0){
      this.errorMessage('You must write your city');
      theDetailsLegal = false;
    }else if (this.zipCodeInputRef.nativeElement.value === undefined || this.zipCodeInputRef.nativeElement.value.length !== 7){
      this.errorMessage('You must write zip on length 7');
      theDetailsLegal = false;
    }else if (this.countryInputRef.nativeElement.value === undefined || this.countryInputRef.nativeElement.value.length === 0){
      this.errorMessage('You must write you country');
      theDetailsLegal = false;
    }else if (this.addressInputRef.nativeElement.value === undefined || this.addressInputRef.nativeElement.value.length === 0){
      this.errorMessage('You must write you address');
      theDetailsLegal = false;
    }else if (this.fullNameInputRef.nativeElement.value === undefined || this.fullNameInputRef.nativeElement.value.length === 0){
      this.errorMessage('You must write you full name');
      theDetailsLegal = false;
    }else if (this.idInputRef.nativeElement.value === undefined || this.idInputRef.nativeElement.value.length !== 9){
      this.errorMessage('You must write you legal id on length 9');
      theDetailsLegal = false;
    }else if (this.ccvInputRef.nativeElement.value === undefined || this.ccvInputRef.nativeElement.value.length !== 3) {
      this.errorMessage('You must write you legal ccv on length 3');
      theDetailsLegal = false;
    } else if (this.expirationMonth.nativeElement.value === undefined){
        this.errorMessage('You must write expiration Month');
        theDetailsLegal = false;
    } else if (this.expirationYear.nativeElement.value === undefined) {
      this.errorMessage('You must write expiration Year');
      theDetailsLegal = false;
    }else if (this.creditCardNumberInputRef.nativeElement.value === undefined ||
      this.creditCardNumberInputRef.nativeElement.value.length !== 9){
      this.errorMessage('You must write your credit card number on length 9');
      theDetailsLegal = false;
    }
    return theDetailsLegal;
  }

}
