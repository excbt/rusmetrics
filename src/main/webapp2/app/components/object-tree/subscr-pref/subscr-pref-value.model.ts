import { SubscrPref } from './subscr-pref.model';

export class SubscrPrefValue {
    constructor(
         devComment?: string,
          id?: number,
         isActive?: boolean,
         subscrPref?: SubscrPref,
         subscrPrefCategory?: string,
         subscrPrefKeyname?: string,
         subscriberId?: number,
         public value?: string,
         version?: number
    ) {}

    public getValue(): string {
        return this.value ? this.value : null;
    }
}
