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
        on: 'hover'
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
    $('.action.input[type="password"] button.icon').click(function(e){
        e.preventDefault();
        let button = $(e.target).closest('button');
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

    // works lock action button
    $('.work-lock').click(function(e){
        e.preventDefault();

        let button = $(e.target).closest('button');
        let form = $(e.target).closest('form');

        if (button.attr('data-work-enabled') === 'true') {

            // disable action: show comment form

            let modal = $('#lock-work-modal');
            modal
                .modal({
                    closable: false,
                    onShow: function(){
                        let title = button.attr('data-work-title');
                        modal.find('.content p').text(title);
                    },
                    onApprove: function(){
                        let comment = modal.find('textarea[name="comment"]').val();
                        form.find('input[name="comment"]').val(comment);
                        form.submit();
                    }
                })
                .modal('show');
        } else {

            // enable action: confirm

            let modal = $('#work-enable-modal');
            let workId = button.attr('data-work-id');
            let addition = modal.find('p.addition').closest('div');
            addition.removeClass('hidden');

            // load lock comment and show it in confirm dialog
            $.ajax(window.UI.baseUrl + (window.UI.baseUrl === '/' ? '' : window.UI.baseUrl) + 'works/' + workId + '/lockcomment')
                .done(function(data) {
                    addition.find('p').text(data.response);
                })
                .fail(function() {
                    // TODO: Add multilingual error message
                    addition.find('p').text("Error / Fehler");
                })
                .always(function () {
                    modal
                        .modal({
                            closable: false,
                            onApprove: function(){
                                form.find('input[name="enabled"]').val('on');
                                form.submit();
                            }
                        })
                        .modal('show');
                });
        }
    });


});
