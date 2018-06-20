/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

"use strict";

$(document).ready(function(){

    // main menu toggle button for mobile view
    $('nav button.toggle').click(function(){
        $('nav.nav').toggleClass('show');
    });

    // initialize all dropdown menus
    $('.ui.dropdown').dropdown({
        on: 'click'
    });

    // initialize language menu dropdown
    $('.nav .ui.dropdown').dropdown({
        on: 'hover',
        action: 'hide'
    });

    // initialize all checkboxes
    $('.ui.checkbox').checkbox();

    // confirm user delete button
    $('a[href*="/users"][href*="/delete"]').click(function(e){
        e.preventDefault();
        $('#user-delete-modal')
            .modal({
                closable: false,
                onApprove: function(){
                    $(e.target).closest('form').submit();
                }
            })
            .modal('show');
    });

    // toggle action for password fields
    $('.action.input input[type="password"]').siblings('button').click(function(e){
        let button = $(this);
        let icon = button.find('i');
        let input = button.siblings('input');

        if (icon.hasClass('slash')) {
            input.attr('type', 'password');
            icon.removeClass('slash')
        } else {
            input.attr('type', 'text');
            icon.addClass('slash')
        }
    });

    // pagination: decrease given page number by 1 because its 0-based
    $('.page-input form').on('submit', function(e){
        let input = $(e.target).find('input[type="text"]');
        let value = input.val();
        input.val(value - 1);
    });

    // pagination: items per page switch
    $('.page-size .dropdown').dropdown({
        onChange: function(value, text, $choice) {
            this.closest('form').submit();
        }
    });

    // works search field: tooltip
    $('form[action="/works"] input[type="text"][name="search"]').popup({
        inline: true,
        hoverable: true
    });

    // works search field: clear button
    $('form[action="/works"] i.times.link.icon').click(function(e){
        $(this).siblings('input').val('');
        $(this).closest('form').submit();
    });

    // works select all or none dropdown
    $('.select-works input').on('change', function(e){
        let input = $(this);
        input.prop('checked', !input.prop('checked'));
    });
    $('.select-works .item').on('click', function(e){
        let input = $(this).find('input');
        $('form .work-items input[name="workIds"]').prop('checked', input.prop('checked'));
    });

    // works action dropdown
    $('.work-actions .dropdown').dropdown({
        action: 'hide'
    });

    let selectWork = function(id) {
        let form = $('form[action="/works"]');
        form.find('input[name="workIds"]').prop('checked', false);
        form.find(`.item[data-work-id=${id}] input[name="workIds"]`).prop('checked', true);
    };

    // works cache delete button
    $('.button.work-cache-delete').click(function(e){
        let work = $(this).closest('.item[data-work-id]');
        selectWork(work.attr('data-work-id'));
    });

    // works lock action button
    $('.work-lock').click(function(e){

        let work = $(this).closest('.item[data-work-id]');
        let workId = work.attr('data-work-id');

        if (work.attr('data-work-enabled') === 'true') {

            // disable action: show comment form

            let modal = $('#lock-work-modal');
            modal
                .modal({
                    closable: false,
                    onShow: function(){
                        modal.find('form input[name="workIds"]').val(workId);
                        let title = work.attr('data-work-title');
                        modal.find('.content p').text(title);
                    }
                })
                .modal('show');
        } else {

            // enable action: confirm

            let modal = $('#work-unlock-modal');
            let addition = modal.find('p.addition');

            let url = `${window.UI.baseUrl}/works/${workId}/lockcomment`
                .replace(/^[\/]+/, '/');

            // load lock comment and show it in confirm dialog
            $.ajax(url)
                .done(function(data) {
                    addition.text(data.response);
                })
                .fail(function(jqXHR, textStatus) {
                    // TODO: Add multilingual error message
                    addition.text('Error / Fehler: ' + textStatus);
                })
                .always(function () {
                    modal
                        .modal({
                            closable: false,
                            onShow: function(){
                                modal.find('form input[name="workIds"]').val(workId);
                            }
                        })
                        .modal('show');
                });
        }
    });


});
