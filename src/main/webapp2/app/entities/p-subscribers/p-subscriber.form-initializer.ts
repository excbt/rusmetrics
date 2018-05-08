import { FormBuilder, FormGroup } from '@angular/forms';
import { PSubscriber } from './p-subscriber.model';
import { ExcFormValue } from '../../shared-blocks';
import { ExcFormInitializer } from '../../shared-blocks/exc-edit-form/exc-form.initializer';

export class PSubscriberFormInitializer extends ExcFormInitializer<PSubscriber> {

    private subscriberMode: string;

    constructor(private fb: FormBuilder) {
        super();
    }

    createForm(data: PSubscriber): FormGroup {
        const form = this.fb.group({
            id: data.id,
            organizationId: [data.organizationId],
            subscriberName: [data.subscriberName],
            subscriberInfo: [data.subscriberInfo],
            subscriberComment: [data.subscriberComment],
            timezoneDef: [data.timezoneDef],
            contactEmail: [data.contactEmail],
            rmaLdapOu: [data.rmaLdapOu]
        });
        return form;
    }

    initForm(): FormGroup {
        const form = this.fb.group({
            id: null,
            organizationId: null,
            subscriberName: null,
            subscriberInfo: null,
            subscriberComment: null,
            timezoneDef: null,
            canCreateChild: false,
            contactEmail: null,
            rmaLdapOu: null,
        });
        return form;
    }

    prepareEntity(form: FormGroup, currEntity: PSubscriber): PSubscriber {
        const formModel = form.value;

        console.log('prepare Entity');

        const savePSubscriber: PSubscriber = {
            id: (currEntity && currEntity.id) ? currEntity.id : null,
            organizationId: ExcFormValue.clearEmptyString(formModel.organizationId as number),
            subscriberName: ExcFormValue.clearEmptyString(formModel.subscriberName as string),
            subscriberInfo: ExcFormValue.clearEmptyString(formModel.subscriberInfo as string),
            subscriberComment: ExcFormValue.clearEmptyString(formModel.subscriberComment as string),
            timezoneDef: ExcFormValue.clearEmptyString(formModel.timezoneDef as string),
            subscrType: null,
            contactEmail: ExcFormValue.clearEmptyString(formModel.contactEmail as string),
            rmaLdapOu: this.subscriberMode === 'RMA' ? ExcFormValue.clearEmptyString(formModel.rmaLdapOu as string) : null,
            canCreateChild: formModel.canCreateChild,
            version: (currEntity && currEntity.version) ? currEntity.version : 0,
            organizationInn: null,
            organizationName: null
        };
        return savePSubscriber;
    }

}
