import {Component, Input, OnInit} from '@angular/core';
import {Receipt} from '../../shared/receipt.model';
import {UserService} from '../../services/user.service';
import {StoreService} from '../../services/store.service';
import {ShareService} from '../../services/share.service';

@Component({
  selector: 'app-history-purchase',
  templateUrl: './history-purchase.component.html',
  styleUrls: ['./history-purchase.component.css']
})
export class HistoryPurchaseComponent implements OnInit {
  receipts: Receipt[];
  receipt: string;
  searchText: string;

  constructor(private userService: UserService, private storeService: StoreService, private shareService: ShareService) {
  }

  ngOnInit(): void {
    if (this.shareService.receipts !== null && !this.userService.isLoggingUser()) {
     this.receipts = this.shareService.receipts;
    } else {
      this.userService.viewPurchaseHistory().subscribe(response => {
        if (response !== undefined && response !== null){
          this.receipts = response;
        }
      });
    }
  }

}
