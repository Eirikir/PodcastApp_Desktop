package podcast_application.media.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import podcast_application.management.helpers.ChannelManager;

import java.util.Optional;

public class ChannelInfoPane extends BorderPane {
    private Label title = new Label(), episodesAmount = new Label(),
            description = new Label();

    private ChannelInterface currentlySelected;

    private boolean showDetails = false;
    private VBox subBox;
    private Button addBtn, removeBtn, detailsBtn;

    public Button getAddBtn() { return addBtn; }
    public Button getRemoveBtn() { return removeBtn; }

    public ChannelInfoPane(ChannelInterface channel) {
        this.getStyleClass().add("channelInfoPane");
        currentlySelected = channel;

        //////////
        addBtn = new Button();
        addBtn.getStyleClass().add("channelInfoBtn");
        addBtn.setId("addChannelBtn");

        addBtn.setOnAction(e -> {
            new AddChannelDialog();
        });

        removeBtn = new Button();
        removeBtn.getStyleClass().add("channelInfoBtn");
        removeBtn.setId("removeChannelBtn");


        removeBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Delete Channel!");
            alert.setContentText("Do you really want to delete channel '"+ currentlySelected.getChannelTitle()+"'?");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK) {
                ChannelManager.getInstance().removeChannel((PodcastChannel) currentlySelected);
                Platform.runLater(()-> {
                    changeChannel(ChannelManager.getInstance().getSelected());
                });

            }
        });

        removeBtn.setManaged(false);
        removeBtn.setVisible(false);

        VBox addBox = new VBox(10, addBtn, removeBtn);
        addBox.setPadding(new Insets(8,20,0,10));
        setLeft(addBox);
        addBox.setAlignment(Pos.TOP_LEFT);

        //////////



        final String LABEL_CLASS = "channelInfoLabelClass";
        title.getStyleClass().add(LABEL_CLASS);
        title.setId("channelInfoTitle");

        detailsBtn = new Button();

        detailsBtn.getStyleClass().add("smallBtnClass");
        detailsBtn.setId("infoBtnDown");


        HBox btnHolder = new HBox(detailsBtn);
        btnHolder.setAlignment(Pos.BOTTOM_LEFT);
        HBox headBox = new HBox(10, title, btnHolder);

        description.getStyleClass().add(LABEL_CLASS);
        description.setId("channelInfoDesc");
        description.setWrapText(true);

        subBox = new VBox(description);
//        description.setVisible(false);
//        subBox.visibleProperty().bind(showDetails);
        subBox.setManaged(false);
        subBox.setVisible(false);

        VBox mainBox = new VBox(5,headBox, subBox);

//        mainBox.setPadding(new Insets(0,0,0,20));

        setCenter(mainBox);

        episodesAmount.getStyleClass().add(LABEL_CLASS);
        episodesAmount.setId("channelEpisodes");

        setRight(episodesAmount);

        detailsBtn.setOnAction(e -> {
            toggleDetails();
/*            showDetails = (showDetails) ? false : true;
            subBox.setManaged(showDetails);
            subBox.setVisible(showDetails);

            // we may not delete playlist channel
            if(!currentlySelected.getChannelTitle().equals("Playlist")) {
                removeBtn.setManaged(showDetails);
                removeBtn.setVisible(showDetails);
            }

            String id = (showDetails) ? "infoBtnUp" : "infoBtnDown";
            detailsBtn.setId(id);
*/
        });

        changeChannel(channel);
    }

    private void toggleDetails() {
        showDetails = (showDetails) ? false : true;
        subBox.setManaged(showDetails);
        subBox.setVisible(showDetails);

        // we may not delete playlist channel
        if(!currentlySelected.getChannelTitle().equals("Playlist")) {
            removeBtn.setManaged(showDetails);
            removeBtn.setVisible(showDetails);
        }

        String id = (showDetails) ? "infoBtnUp" : "infoBtnDown";
        detailsBtn.setId(id);
    }


    public void changeChannel(ChannelInterface channel) {
        if(showDetails)
            toggleDetails();

        currentlySelected = channel;
        title.setText(channel.getChannelTitle());
        episodesAmount.setText(channel.getAmountOfEpisodes()+" episodes");
        description.setText(channel.getChannelDescription());

    }
}
