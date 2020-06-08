import {Component, ElementRef, Input, OnInit, ViewChild, Inject} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-amount-products',
  templateUrl: './amount-products.component.html',
  styleUrls: ['./amount-products.component.css']
})
export class AmountProductsComponent implements OnInit {

  @ViewChild('amount', {static: false}) amount: ElementRef;
  @Input() name: string;
  isNotWith: boolean;
  constructor( public dialogRef: MatDialogRef<AmountProductsComponent>) {
    this.isNotWith = true;
  }

  ngOnInit(): void {
  }

  onOk() {
    this.dialogRef.close({isNotWith: this.isNotWith, amount: this.amount.nativeElement.value});
  }

  onChange() {
    this.isNotWith = !this.isNotWith;
    console.log(this.isNotWith);
    if (!this.isNotWith) {
      this.name = 'With';
    }else{
      this.name = 'Without';
    }
    console.log(this.name);
  }
}
