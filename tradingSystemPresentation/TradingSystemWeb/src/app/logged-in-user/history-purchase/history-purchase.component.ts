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
  @Input() wantStoreHistory: boolean;

  constructor(private userService: UserService, private storeService: StoreService, private shareService: ShareService) {
    this.wantStoreHistory = false;
  }

  ngOnInit(): void {
    if (this.wantStoreHistory) {
      this.receipts = this.storeService.viewPurchaseHistory();
    } else if (!this.userService.isLoggingUser()) {
      this.receipts = this.shareService.receipts;
    } else {
      this.receipts = this.userService.viewPurchaseHistory();
    }
  }

}
