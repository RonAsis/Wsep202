import {Component, Input, OnInit} from '@angular/core';
import {Product} from '../../../shared/product.model';
import {Receipt} from '../../../shared/receipt.model';

@Component({
  selector: 'app-receipt-element',
  templateUrl: './receipt-element.component.html',
  styleUrls: ['./receipt-element.component.css']
})
export class ReceiptElementComponent implements OnInit {
  @Input() receipt: Receipt;
  product: string;

  constructor() { }

  ngOnInit(): void {
  }

  onSelected() {

  }
}
